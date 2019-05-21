package com.nix.jingxun.addp.web.service;

import cn.hutool.core.util.StrUtil;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.ssh.common.util.ShellUtil;
import com.nix.jingxun.addp.web.common.ShellExeLog;
import com.nix.jingxun.addp.web.diamond.ReleasePhase;
import com.nix.jingxun.addp.web.diamond.ReleaseType;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IReleaseBillService;
import com.nix.jingxun.addp.web.iservice.IServicesService;
import com.nix.jingxun.addp.web.jpa.ReleaseBillJpa;
import com.nix.jingxun.addp.web.model.ChangeBranchModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ReleaseBillModel;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Resource;

/**
 * @author keray
 * @date 2019/05/20 18:17
 */
@Slf4j
public class ReleaseBillServiceImpl extends BaseServiceImpl<ReleaseBillModel, Long> implements IReleaseBillService {

    @Resource
    private ReleaseBillJpa releaseBillJpa;
    @Resource
    private IServicesService servicesService;
    @Resource
    private IProjectsService projectsService;


    @Override
    public ReleaseBillModel deployBranch(ChangeBranchModel branchModel) throws Exception {
        return null;
    }

    public boolean pullCode(ReleaseBillModel releaseBillModel, ShellExe shellExe) throws Exception {
        if (releaseBillModel.getReleasePhase() != ReleasePhase.init) {
            return listener(releaseBillModel, shellExe);
        }
        ChangeBranchModel changeBranchModel = releaseBillModel.getChangeBranchModel();
        if (!projectsService.cdRoot(changeBranchModel.getProjectsModel(),shellExe)) {
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
                            servicesService.gitAuth(shellExe, changeBranchModel.getProjectsModel());
                        }
                    }, ShellExeLog.fail);
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


    public boolean build(ReleaseBillModel releaseBillModel, ShellExe shellExe) throws Exception {
        if (releaseBillModel.getReleasePhase() != ReleasePhase.pullCode) {
            return listener(releaseBillModel, shellExe);
        }
        ChangeBranchModel changeBranchModel = releaseBillModel.getChangeBranchModel();
        if (!projectsService.cdRoot(changeBranchModel.getProjectsModel(),shellExe)) {
            return false;
        }
        ProjectsModel projectsModel = changeBranchModel.getProjectsModel();
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
                                if (r.toString().matches("")) {
                                    ShellExeLog.success.accept(r, c);
                                } else {
                                    ShellExeLog.fail.accept(r, c);
                                }
                            }, ShellExeLog.fail);
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


    public boolean listener(ReleaseBillModel releaseBillModel, ShellExe shellExe) throws Exception {
        if (releaseBillModel.getReleasePhase() != ReleasePhase.build) {
            return true;
        }
        ChangeBranchModel changeBranchModel = releaseBillModel.getChangeBranchModel();
        if (!projectsService.cdRoot(changeBranchModel.getProjectsModel(),shellExe)) {
            return false;
        }
        releaseBillModel.setReleasePhase(ReleasePhase.start);
        releaseBillModel.setReleaseType(ReleaseType.run);
        update(releaseBillModel);
        try {
            shellExe.AsyncExecute(StrUtil.format("docker logs -f --tail \"10\" {}-{}",
                    changeBranchModel.getProjectsModel().getName(), releaseBillModel.getEnvironment().name()), (r, c) -> {
                if (r.toString().contains("Tomcat started on port(s)")) {
                    ShellExeLog.success.accept(r, c);
                    // 启动成功后ctrl+c停止
                    shellExe.ctrlC();
                } else {
                    ShellExeLog.fail.accept(r, c);
                }
            }, ShellExeLog.fail);
            releaseBillModel.setReleaseType(ReleaseType.releaseSuccess);
            update(releaseBillModel);
        } catch (Exception e) {
            log.error("发布单发布第三阶段失败", e);
            releaseBillModel.setReleaseType(ReleaseType.releaseFail);
            update(releaseBillModel);
            return false;
        }
        return false;
    }

    @Override
    protected JpaRepository<ReleaseBillModel, Long> jpa() {
        return releaseBillJpa;
    }
}
