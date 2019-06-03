package com.nix.jingxun.addp.web.service;

import cn.hutool.core.lang.Assert;
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
import com.nix.jingxun.addp.web.common.supper.WebThreadPool;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IReleaseBillService;
import com.nix.jingxun.addp.web.iservice.IReleaseServerStatusService;
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
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
    @Resource
    private IReleaseBillService releaseBillService;
    @Resource
    private IReleaseServerStatusService releaseServerStatusService;


    @Override
    public ReleaseBillModel deployBranch(ReleaseBillModel releaseBillModel, Consumer<ReleaseBillModel> successCallback, Consumer<ReleaseBillModel> failCallback) throws Exception {
        if (releaseBillModel.getEnvironment() == ADDPEnvironment.pro) {
            releaseBillModel.setReleaseTime(null);
            releaseBillJpa.saveAndFlush(releaseBillModel);
            return proBuild(releaseBillModel.getId(), successCallback, failCallback);
        }
        // 非线上发布自动将发布时时间改为现在
        releaseBillModel.setReleaseTime(LocalDateTime.now());
        releaseBillService.update(releaseBillModel);
        WebThreadPool.IO_THREAD.execute(() -> {
            try {
                if (pullCode(releaseBillModel) && build(releaseBillModel) && startApp(releaseBillModel)) {
                    log.info("全自动部署完成");
                    successCallback.accept(releaseBillModel);
                } else {
                    failCallback.accept(releaseBillModel);
                }
            } catch (Exception e) {
                e.printStackTrace();
                failCallback.accept(releaseBillModel);
            }
        });
        return releaseBillModel;
    }

    @Override
    public boolean pullCode(ReleaseBillModel releaseBillModel) throws Exception {
        try {

            releaseBillModel.setReleasePhase(ReleasePhase.pullCode);
            releaseBillModel.setReleaseType(ReleaseType.run);
            releaseBillService.update(releaseBillModel);
            // 根据当前环境得到项目的服务器组
            List<ServerModel> serverModels = servicesService.selectEnvAllowServer(releaseBillModel._getChangeBranchModel()._getProjectsModel()
                    , releaseBillModel.getEnvironment())
                    .stream()
                    .filter(s -> s.getEnvironment() != ADDPEnvironment.bak)
                    .collect(Collectors.toList());
            boolean result = servicesService.moreServiceExec(
                    serverModels,
                    (server) -> {
                        try {
                            releaseServerStatusService.setCurrentStatus(releaseBillModel, server, ReleasePhase.pullCode, ReleaseType.run);
                            if (!pullCode(releaseBillModel, servicesService.shellExeByUsername(server))) {
                                throw new ShellNoSuccessException("第一阶段执行不成功");
                            }
                            releaseServerStatusService.setCurrentStatus(releaseBillModel, server, ReleasePhase.pullCode, ReleaseType.releaseSuccess);
                        } catch (Exception e) {
                            releaseServerStatusService.setCurrentStatus(releaseBillModel, server, ReleasePhase.pullCode, ReleaseType.releaseFail);
                            throw new ShellExeException(e);
                        }
                    });
            if (!result) {
                log.error("部署第一阶段失败 : {}", releaseBillModel);
                releaseBillModel.setReleaseType(ReleaseType.releaseFail);
                releaseBillService.update(releaseBillModel);
            } else {
                releaseBillModel.setReleaseType(ReleaseType.releaseSuccess);
                releaseBillService.update(releaseBillModel);
            }
            return result;
        } catch (Exception e) {
            log.error("部署第一阶段失败 : {}", releaseBillModel);
            releaseBillModel.setReleaseType(ReleaseType.releaseFail);
            releaseBillService.update(releaseBillModel);
            return false;
        }
    }

    @Override
    public boolean build(ReleaseBillModel releaseBillModel) throws Exception {
        try {
            releaseBillModel.setReleasePhase(ReleasePhase.build);
            releaseBillModel.setReleaseType(ReleaseType.run);
            releaseBillService.update(releaseBillModel);
            boolean result = servicesService.moreServiceExec(
                    servicesService.selectEnvAllowServer(releaseBillModel._getChangeBranchModel()._getProjectsModel(),
                            releaseBillModel.getEnvironment())
                            .stream()
                            .filter(s -> s.getEnvironment() != ADDPEnvironment.bak)
                            .collect(Collectors.toList()),
                    (server) -> {
                        try {
                            releaseServerStatusService.setCurrentStatus(releaseBillModel, server, ReleasePhase.build, ReleaseType.run);
                            if (!build(releaseBillModel, servicesService.shellExeByUsername(server))) {
                                throw new ShellNoSuccessException("第二阶段执行不成功");
                            }
                            releaseServerStatusService.setCurrentStatus(releaseBillModel, server, ReleasePhase.build, ReleaseType.releaseSuccess);
                        } catch (Exception e) {
                            releaseServerStatusService.setCurrentStatus(releaseBillModel, server, ReleasePhase.build, ReleaseType.releaseFail);
                            throw new ShellExeException(e);
                        }
                    });
            if (!result) {
                log.error("部署第二阶段失败 : {}", releaseBillModel);
                releaseBillModel.setReleaseType(ReleaseType.releaseFail);
                releaseBillService.update(releaseBillModel);
            } else {

                releaseBillModel.setReleaseType(ReleaseType.releaseSuccess);
                releaseBillService.update(releaseBillModel);
            }
            return result;
        } catch (Exception e) {
            log.error("部署第二阶段失败 : {}", releaseBillModel);
            releaseBillModel.setReleaseType(ReleaseType.releaseFail);
            releaseBillService.update(releaseBillModel);
        }
        return false;
    }

    @Override
    public boolean startApp(ReleaseBillModel releaseBillModel) {
        Supplier<Boolean> work = () -> {
            try {
                releaseBillModel.setReleasePhase(ReleasePhase.start);
                releaseBillModel.setReleaseType(ReleaseType.run);
                releaseBillService.update(releaseBillModel);
                boolean result = servicesService.moreServiceExec(
                        servicesService.selectEnvAllowServer(releaseBillModel._getChangeBranchModel()._getProjectsModel(), releaseBillModel.getEnvironment()),
                        (server) -> {
                            try {
                                releaseServerStatusService.setCurrentStatus(releaseBillModel,server,ReleasePhase.start,ReleaseType.run);
                                if (!startApp(releaseBillModel, servicesService.shellExeByUsername(server))) {
                                    throw new ShellNoSuccessException("第三阶段执行不成功");
                                }
                                releaseServerStatusService.setCurrentStatus(releaseBillModel,server,ReleasePhase.start,ReleaseType.releaseSuccess);
                            } catch (Exception e) {
                                releaseServerStatusService.setCurrentStatus(releaseBillModel,server,ReleasePhase.start,ReleaseType.releaseFail);
                                throw new ShellExeException(e);
                            }
                        });
                if (!result) {
                    log.error("部署第三阶段失败 : {}", releaseBillModel);
                    releaseBillModel.setReleaseType(ReleaseType.releaseFail);
                    releaseBillService.update(releaseBillModel);
                } else {
                    releaseBillModel.setReleaseType(ReleaseType.releaseSuccess);
                    releaseBillService.update(releaseBillModel);
                }
                return result;
            } catch (Exception e) {
                log.error("部署第三阶段失败 : {}", releaseBillModel);
                releaseBillModel.setReleaseType(ReleaseType.releaseFail);
                try {
                    releaseBillService.update(releaseBillModel);
                } catch (Exception ignore) {
                }
                throw new RuntimeException(e);
            }
        };
        // 发布单发布时间在此之前
        if (releaseBillModel.getReleaseTime().isBefore(LocalDateTime.now())) {
            return work.get();
        }
        return false;
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
        ReleaseBillModel bill = releaseBillJpa.selectChangeBill(model.getId(), environment);
        if (bill != null) {
            return bill;
        }
        MemberModel member = MemberCache.currentUser();
        return releaseBillService.save(ReleaseBillModel.builder()
                .changeBranchId(model.getId())
                .environment(environment)
                .memberId(member.getId())
                .releasePhase(ReleasePhase.init)
                .releaseType(ReleaseType.wait)
                .releaseTime(LocalDateTime.now())
                .build());
    }

    @Override
    public ReleaseBillModel selectProjectBill(Long projectId, ADDPEnvironment environment) {
        return releaseBillJpa.selectProjectBill(projectId, environment.name());
    }

    @Override
    public ReleaseBillModel billDown(Long billId) throws Exception {
        ReleaseBillModel bill = findById(billId);
        if (bill.getReleaseType() != ReleaseType.releaseSuccess) {
            return bill;
        }
        if (servicesService.moreServiceExec(bill._getChangeBranchModel()._getProjectsModel()._getServerModels(),
                (serverModel) -> {
                    try {
                        if (!billDown(bill, servicesService.shellExeByUsername(serverModel))) {
                            throw new ShellNoSuccessException("应用停止失败");
                        }
                    } catch (Exception e) {
                        throw new ShellExeException(e);
                    }
                })) {
            bill.setReleasePhase(ReleasePhase.init);
        }
        return super.update(bill);
    }

    @Override
    public ReleaseBillModel proBuild(Long id, Consumer<ReleaseBillModel> successCallback, Consumer<ReleaseBillModel> failCallback) {
        ReleaseBillModel releaseBillModel = findById(id);
        WebThreadPool.IO_THREAD.execute(() -> {
            try {
                if (pullCode(releaseBillModel) && build(releaseBillModel)) {
                    log.info("线上发布一二阶段完成");
                    // 成功后将显示机器全部改为不允许发布
                    servicesService.selectEnvAllowServer(releaseBillModel._getChangeBranchModel()._getProjectsModel(), releaseBillModel.getEnvironment())
                            .forEach(s -> {
                                s.setAllowRestart(false);
                                try {
                                    servicesService.update(s);
                                } catch (Exception e) {
                                    releaseBillModel.setReleaseType(ReleaseType.releaseFail);
                                    try {
                                        releaseBillService.update(releaseBillModel);
                                    } catch (Exception ignore) {
                                    }
                                }
                            });
                    successCallback.accept(releaseBillModel);
                } else {
                    failCallback.accept(releaseBillModel);
                }
            } catch (Exception e) {
                e.printStackTrace();
                failCallback.accept(releaseBillModel);
            }
        });
        return releaseBillModel;
    }

    @Override
    public int getAllBatch(ReleaseBillModel releaseBillModel) {
        return 2;
    }

    @Override
    public ReleaseBillModel proStart(ReleaseBillModel releaseBillModel, boolean skip) {
        Supplier<ReleaseBillModel> work = () -> {
            int allBatch = getAllBatch(releaseBillModel);
            WebThreadPool.IO_THREAD.execute(() -> {
                try {
                    int i = releaseBillModel.getReleaseType() == ReleaseType.stop ? 1 : 0;
                    for (; i < allBatch; i++) {
                        proBatchRelease(releaseBillModel, i);
                        if (i == 0) {
                            releaseBillModel.setReleaseType(ReleaseType.stop);
                            update(releaseBillModel);
                            if (!skip) {
                                break;
                            }
                        }
                    }
                    if (i == allBatch) {
                        // 发布完成后将服务器跟改为可发布状态
                        for (ServerModel model : releaseBillModel._getChangeBranchModel()._getProjectsModel()._getServerModels()) {
                            model.setAllowRestart(true);
                            servicesService.update(model);
                        }
                    }
                } catch (Exception ignore) {
                }
            });
            return releaseBillModel;
        };
        if (releaseBillModel.getReleaseTime().isBefore(LocalDateTime.now())) {
            return work.get();
        } else {
            return releaseBillModel;
        }
    }

    @Override
    public ReleaseBillModel proBatchRelease(ReleaseBillModel releaseBillModel, Integer batchNum) {
        List<ServerModel> proAllServer = servicesService.selectAllServes(releaseBillModel._getChangeBranchModel()._getProjectsModel(), ADDPEnvironment.pro);
        // 暂时只支持分2批发布  后面单独写线上发布单
        List<ServerModel> workServers = batchServers(proAllServer, batchNum, 2);
        workServers.forEach(serverModel -> {
            serverModel.setAllowRestart(true);
            try {
                servicesService.update(serverModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Assert.isTrue(startApp(releaseBillModel));
        workServers.forEach(serverModel -> {
            serverModel.setAllowRestart(false);
            try {
                servicesService.update(serverModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return releaseBillModel;
    }

    /*****************************************************************************************************************************************/
    /******************************************************private****************************************************************************/
    /******************************************************private****************************************************************************/
    /******************************************************private****************************************************************************/
    /*****************************************************************************************************************************************/
    private boolean pullCode(ReleaseBillModel releaseBillModel, ShellExe shellExe) throws Exception {
        ChangeBranchModel changeBranchModel = releaseBillModel._getChangeBranchModel();
        if (!projectsService.cdRoot(changeBranchModel._getProjectsModel(), shellExe)) {
            return false;
        }
        try {
            // 切换分支
            shellExe.syncExecute("git checkout .", ShellExeLog.success, ShellExeLog.fail)
                    .syncExecute(StrUtil.format("git checkout {}", changeBranchModel.getBranchName()), ShellExeLog.success, ShellExeLog.fail)
                    // pull 代码
                    .syncExecute("git pull", (r, c) -> {
                        ShellExeLog.success.accept(r, c);
                        if (ShellUtil.shellNeedKeydown(r.toString())) {
                            servicesService.gitAuth(shellExe, changeBranchModel._getProjectsModel());
                        }
                    }, ShellExeLog.fail)
                    .close();
            return true;
        } catch (Exception e) {
            log.error("发布单发布第一阶段失败", e);
            return false;
        }
    }


    private boolean build(ReleaseBillModel releaseBillModel, ShellExe shellExe) throws Exception {
        ChangeBranchModel changeBranchModel = releaseBillModel._getChangeBranchModel();
        if (!projectsService.cdRoot(changeBranchModel._getProjectsModel(), shellExe)) {
            return false;
        }
        try {
            // mvn 打包 项目路径下
            shellExe.syncExecute(StrUtil.format("mvn clean package -P {} -DskipTests", releaseBillModel.getEnvironment().name()), (r, c) -> {
                if (!r.toString().contains("BUILD SUCCESS")) {
                    ShellExeLog.fail.accept(r, c);
                }
                ShellExeLog.success.accept(r, c);
            }, ShellExeLog.fail)
                    .close();
            return true;
        } catch (Exception e) {
            log.error("发布单发布第二阶段失败", e);
            return false;
        }
    }


    private boolean startApp(ReleaseBillModel releaseBillModel, ShellExe shellExe) throws Exception {
        ChangeBranchModel changeBranchModel = releaseBillModel._getChangeBranchModel();
        // 如果是备份主机的启动  设置环境为bak
        boolean isBak = false;
        if (releaseBillModel.getEnvironment() == ADDPEnvironment.pro) {
            List<ServerModel> allowServers = servicesService.selectEnvAllowServer(changeBranchModel._getProjectsModel(), ADDPEnvironment.pro);
            if (allowServers.size() == 1 && allowServers.get(0).getEnvironment() == ADDPEnvironment.bak) {
                isBak = true;
            }
        }
        ADDPEnvironment nowEnv = isBak ? ADDPEnvironment.bak : releaseBillModel.getEnvironment();
        ProjectsModel projectsModel = releaseBillModel._getChangeBranchModel()._getProjectsModel();
        if (!projectsService.cdRoot(changeBranchModel._getProjectsModel(), shellExe)) {
            return false;
        }
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean result = new AtomicBoolean(false);
        try {
            shellExe.oneCmd(StrUtil.format("docker stop {}-{}", changeBranchModel.getProjectsModel().getName(), nowEnv.name()));
            shellExe.oneCmd(StrUtil.format("docker rm {}-{}", changeBranchModel.getProjectsModel().getName(), nowEnv.name()));
            shellExe
                    //执行build.sh脚本
                    .syncExecute(StrUtil.format("bash ./ADDP-INF/build.sh {} {} {}",
                            projectsModel.getName(), nowEnv.name(), nowEnv.getPort()),
                            (r, c) -> {
                                if (r.toString().contains("Successfully")) {
                                    ShellExeLog.success.accept(r, c);
                                } else {
                                    ShellExeLog.fail.accept(r, c);
                                }
                            }, ShellExeLog.fail).syncExecute(StrUtil.format("bash ./ADDP-INF/start.sh {} {} {} {}",
                    changeBranchModel.getProjectsModel().getName(), nowEnv.name()
                    , nowEnv.getPort(), changeBranchModel.getPort()),
                    (r, c) -> {
                        if (r.toString().matches("[\\S\\s]+[\\w]{64}[\\S\\s]*")) {
                            ShellExeLog.success.accept(r, c);
                            return;
                        }
                        ShellExeLog.fail.accept(r, c);
                    }, ShellExeLog.fail)
                    .AsyncExecute(StrUtil.format("docker logs -f --tail \"100\" {}-{}",
                            changeBranchModel._getProjectsModel().getName(), nowEnv.name()), (r, c) -> {
                        if (r.toString().contains("Tomcat started on port(s)") || r.toString().contains("Welcome To")) {
                            ShellExeLog.success.accept(r, c);
                            result.set(true);
                            // 启动成功后ctrl+c停止
                            shellExe.ctrlC();
                        }
                    }, ShellExeLog.fail, (r, c) -> latch.countDown());
        } catch (Exception e) {
            log.error("发布单发布第三阶段失败", e);
            return false;
        }
        latch.await(5, TimeUnit.MINUTES);
        shellExe.close();
        return result.get();
    }

    private boolean billDown(ReleaseBillModel bill, ShellExe shellExe) {
        ChangeBranchModel changeBranchModel = bill._getChangeBranchModel();
        if (!projectsService.cdRoot(changeBranchModel._getProjectsModel(), shellExe)) {
            return false;
        }
        shellExe.syncExecute(StrUtil.format("bash ./ADDP-INF/stop.sh {} {}",
                changeBranchModel._getProjectsModel().getName(), bill.getEnvironment()), (r, c) -> {
            if (r.toString().contains("Error")) {
                ShellExeLog.fail.accept(r, c);
            }
            ShellExeLog.success.accept(r, c);
        }, ShellExeLog.fail);
        return true;
    }


    private List<ServerModel> batchServers(List<ServerModel> servers, int nowBatch, int allBatch) {
        int sum = servers.size();
        int startIndex = nowBatch * (sum / allBatch);
        int endIndex = startIndex + sum / allBatch;
        endIndex = endIndex > sum ? sum : endIndex;
        return servers.subList(startIndex, endIndex);
    }

    @Override
    protected JpaRepository<ReleaseBillModel, Long> jpa() {
        return releaseBillJpa;
    }
}
