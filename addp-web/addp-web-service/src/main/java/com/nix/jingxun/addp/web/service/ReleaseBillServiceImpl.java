package com.nix.jingxun.addp.web.service;

import cn.hutool.core.util.StrUtil;
import com.nix.jingxun.addp.ssh.common.exception.ShellExeException;
import com.nix.jingxun.addp.ssh.common.exception.ShellNoSuccessException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.ssh.common.util.ShellUtil;
import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.common.ShellExeLog;
import com.nix.jingxun.addp.web.IEnum.ReleasePhase;
import com.nix.jingxun.addp.web.IEnum.ReleaseType;
import com.nix.jingxun.addp.web.common.cache.MemberCache;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IReleaseBillService;
import com.nix.jingxun.addp.web.iservice.IServerService;
import com.nix.jingxun.addp.web.jpa.ReleaseBillJpa;
import com.nix.jingxun.addp.web.model.*;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author keray
 * @date 2019/05/20 18:17
 */
@Slf4j
@Service
public class ReleaseBillServiceImpl extends BaseServiceImpl<ReleaseBillModel, Long> implements IReleaseBillService {

    @Resource
    private ReleaseBillJpa releaseBillJpa;
    @Resource
    private IServerService servicesService;
    @Resource
    private IProjectsService projectsService;


    @Override
    public ReleaseBillModel deployBranch(ReleaseBillModel releaseBillModel) throws Exception {
        releaseBillModel.setReleasePhase(ReleasePhase.init);
        return update(releaseBillModel);
    }

    @Override
    public boolean pullCode(ReleaseBillModel releaseBillModel) throws Exception {
        // 根据当前环境得到项目的服务器组
        List<ServerModel> serverModels = servicesService.selectEnvServices(releaseBillModel._getChangeBranchModel()._getProjectsModel(), releaseBillModel.getEnvironment());
        boolean result = servicesService.moreServiceExec(
                serverModels,
                (service) -> {
                    try {
                        if (!pullCode(releaseBillModel, servicesService.shellExeByUsername(service))) {
                            throw new ShellNoSuccessException("第一阶段执行不成功");
                        }
                    } catch (Exception e) {
                        throw new ShellExeException(e);
                    }
                });
        if (!result) {
            log.error("部署第一阶段失败 : {}", releaseBillModel);
        }
        return result;
    }

    @Override
    public boolean build(ReleaseBillModel releaseBillModel) throws Exception {
        boolean result = servicesService.moreServiceExec(
                servicesService.selectEnvServices(releaseBillModel._getChangeBranchModel()._getProjectsModel(), releaseBillModel.getEnvironment()), (service) -> {
                    try {
                        if (!build(releaseBillModel, servicesService.shellExeByUsername(service))) {
                            throw new ShellNoSuccessException("第二阶段执行不成功");
                        }
                    } catch (Exception e) {
                        throw new ShellExeException(e);
                    }
                });
        if (!result) {
            log.error("部署第二阶段失败 : {}", releaseBillModel);
        }
        return result;
    }

    @Override
    public boolean startApp(ReleaseBillModel releaseBillModel) throws Exception {
        boolean result = servicesService.moreServiceExec(
                servicesService.selectEnvServices(releaseBillModel._getChangeBranchModel()._getProjectsModel(), releaseBillModel.getEnvironment()), (service) -> {
                    try {
                        if (!startApp(releaseBillModel, servicesService.shellExeByUsername(service))) {
                            throw new ShellNoSuccessException("第三阶段执行不成功");
                        }
                    } catch (Exception e) {
                        throw new ShellExeException(e);
                    }
                });
        if (!result) {
            log.error("部署第三阶段失败 : {}", releaseBillModel);
        }
        return result;
    }

    @Override
    public ReleaseBillModel changeBill(Long changeId, ADDPEnvironment environment) {

        return releaseBillJpa.findOne(Example.of(
                ReleaseBillModel.builder()
                        .changeBranchId(changeId)
                        .environment(environment)
                        .build()))
                .orElse(null);
    }


    @Override
    public ReleaseBillModel createBill(ChangeBranchModel model, ADDPEnvironment environment) throws Exception {
        MemberModel member = MemberCache.currentUser();
        return save(ReleaseBillModel.builder()
                .changeBranchId(model.getId())
                .environment(environment)
                .memberId(member.getId())
                .releasePhase(ReleasePhase.init)
                .releaseTime(LocalDateTime.now())
        .build());
    }

    private boolean pullCode(ReleaseBillModel releaseBillModel, ShellExe shellExe) throws Exception {
        ChangeBranchModel changeBranchModel = releaseBillModel._getChangeBranchModel();
        if (!projectsService.cdRoot(changeBranchModel._getProjectsModel(), shellExe)) {
            return false;
        }
        releaseBillModel.setReleasePhase(ReleasePhase.pullCode);
        releaseBillModel.setReleaseType(ReleaseType.run);
        update(releaseBillModel);
        try {
            // 切换分支
            shellExe.syncExecute(StrUtil.format("git checkout {}", changeBranchModel.getBranchName()), ShellExeLog.success, ShellExeLog.fail)
                    // pull 代码
                    .syncExecute("git pull", (r, c) -> {
                        ShellExeLog.success.accept(r, c);
                        if (ShellUtil.shellNeedKeydown(r.toString())) {
                            servicesService.gitAuth(shellExe, changeBranchModel._getProjectsModel());
                        }
                    }, ShellExeLog.fail)
                    .close();
            releaseBillModel.setReleaseType(ReleaseType.releaseSuccess);
            update(releaseBillModel);
            return true;
        } catch (Exception e) {
            log.error("发布单发布第一阶段失败", e);
            releaseBillModel.setReleaseType(ReleaseType.releaseFail);
            update(releaseBillModel);
            return false;
        }
    }


    private boolean build(ReleaseBillModel releaseBillModel, ShellExe shellExe) throws Exception {
        ChangeBranchModel changeBranchModel = releaseBillModel._getChangeBranchModel();
        if (!projectsService.cdRoot(changeBranchModel._getProjectsModel(), shellExe)) {
            return false;
        }
        ProjectsModel projectsModel = changeBranchModel._getProjectsModel();
        releaseBillModel.setReleasePhase(ReleasePhase.build);
        releaseBillModel.setReleaseType(ReleaseType.run);
        update(releaseBillModel);
        try {
            // mvn 打包 项目路径下
            shellExe.syncExecute(StrUtil.format("mvn clean package -P {}", releaseBillModel.getEnvironment().name()), (r, c) -> {
                if (!r.toString().contains("BUILD SUCCESS")) {
                    ShellExeLog.fail.accept(r, c);
                }
                ShellExeLog.success.accept(r, c);
            }, ShellExeLog.fail)
                    //执行build.sh脚本
                    .syncExecute(StrUtil.format("bash ./ADDP-INF/build.sh {} {} {}",
                            projectsModel.getName(), releaseBillModel.getEnvironment().name(), releaseBillModel.getEnvironment().getPort()),
                            (r, c) -> {
                                if (r.toString().contains("Successfully")) {
                                    ShellExeLog.success.accept(r, c);
                                } else {
                                    ShellExeLog.fail.accept(r, c);
                                }
                            }, ShellExeLog.fail)
                    .close();
            releaseBillModel.setReleaseType(ReleaseType.releaseSuccess);
            update(releaseBillModel);
            return true;
        } catch (Exception e) {
            log.error("发布单发布第二阶段失败", e);
            releaseBillModel.setReleaseType(ReleaseType.releaseFail);
            update(releaseBillModel);
            return false;
        }
    }


    private boolean startApp(ReleaseBillModel releaseBillModel, ShellExe shellExe) throws Exception {
        ChangeBranchModel changeBranchModel = releaseBillModel._getChangeBranchModel();
        if (!projectsService.cdRoot(changeBranchModel._getProjectsModel(), shellExe)) {
            return false;
        }
        releaseBillModel.setReleasePhase(ReleasePhase.start);
        releaseBillModel.setReleaseType(ReleaseType.run);
        update(releaseBillModel);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean result = new AtomicBoolean(true);
        try {
            shellExe.oneCmd(StrUtil.format("docker stop {}-{}", changeBranchModel.getProjectsModel().getName(), releaseBillModel.getEnvironment().name()));
            shellExe.oneCmd(StrUtil.format("docker rm {}-{}", changeBranchModel.getProjectsModel().getName(), releaseBillModel.getEnvironment().name()));
            shellExe.syncExecute(StrUtil.format("bash ./ADDP-INF/start.sh {} {} {}",
                    changeBranchModel.getProjectsModel().getName(), releaseBillModel.getEnvironment().name(), releaseBillModel.getEnvironment().getPort()),
                    (r, c) -> {
                        if (r.toString().matches("[\\S\\s]+[\\w]{64}[\\S\\s]*")) {
                            ShellExeLog.success.accept(r, c);
                            return;
                        }
                        ShellExeLog.fail.accept(r, c);
                    }, ShellExeLog.fail)
                    .AsyncExecute(StrUtil.format("docker logs -f --tail \"100\" {}-{}",
                            changeBranchModel._getProjectsModel().getName(), releaseBillModel.getEnvironment().name()), (r, c) -> {
                        if (r.toString().contains("Tomcat started on port(s)")) {
                            ShellExeLog.success.accept(r, c);
                            result.set(true);
                            // 启动成功后ctrl+c停止
                            shellExe.ctrlC();
                        }
                    }, (e, c) -> {
                        result.set(false);
                        ShellExeLog.fail.accept(e, c);
                    }, (r, c) -> latch.countDown());
            releaseBillModel.setReleaseType(ReleaseType.releaseSuccess);
            update(releaseBillModel);
        } catch (Exception e) {
            log.error("发布单发布第三阶段失败", e);
            releaseBillModel.setReleaseType(ReleaseType.releaseFail);
            update(releaseBillModel);
            return false;
        }
        latch.await(5, TimeUnit.MINUTES);
        shellExe.close();
        return result.get();
    }

    @Override
    protected JpaRepository<ReleaseBillModel, Long> jpa() {
        return releaseBillJpa;
    }
}
