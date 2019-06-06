package com.nix.jingxun.addp.web.service;

import cn.hutool.core.util.StrUtil;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.web.IEnum.ReleasePhase;
import com.nix.jingxun.addp.web.IEnum.ReleaseType;
import com.nix.jingxun.addp.web.common.mq.MQProducer;
import com.nix.jingxun.addp.web.common.supper.WebThreadPool;
import com.nix.jingxun.addp.web.iservice.IReleaseBillService;
import com.nix.jingxun.addp.web.iservice.IReleaseServerStatusService;
import com.nix.jingxun.addp.web.iservice.IServerService;
import com.nix.jingxun.addp.web.jpa.ReleaseBillJpa;
import com.nix.jingxun.addp.web.jpa.ReleaseServerStatusJpa;
import com.nix.jingxun.addp.web.model.ReleaseBillModel;
import com.nix.jingxun.addp.web.model.ReleaseServerStatusModel;
import com.nix.jingxun.addp.web.model.ServerModel;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author keray
 * @date 2019/06/03 12:10
 */
@Slf4j
@Service
public class ReleaseServerStatusService extends BaseServiceImpl<ReleaseServerStatusModel, Long> implements IReleaseServerStatusService {
    @Resource
    private ReleaseServerStatusJpa releaseServerStatusJpa;

    @Resource
    private IReleaseServerStatusService releaseServerStatusService;

    @Resource
    private IReleaseBillService releaseBillService;
    @Resource
    private IServerService serverService;
    @Resource
    private ReleaseBillJpa releaseBillJpa;
    @Resource
    private MQProducer producer;

    @Override
    protected JpaRepository<ReleaseServerStatusModel, Long> jpa() {
        return releaseServerStatusJpa;
    }


    @Override
    public List<ReleaseServerStatusModel> selectBillAllStatus(ReleaseBillModel releaseBillModel) {
        return releaseServerStatusJpa.findAll(Example.of(ReleaseServerStatusModel.builder()
                .billId(releaseBillModel.getId())
                .build()));
    }

    @Override
    @Transactional
    public void setCurrentStatus(ReleaseBillModel billModel, ServerModel serverModel, ReleasePhase releasePhase, ReleaseType releaseType) {
        try {
            ReleaseServerStatusModel model = getByBillServer(billModel, serverModel);
            model.setReleasePhase(releasePhase);
            model.setReleaseType(releaseType);
            LocalDateTime now = LocalDateTime.now();
            switch (releasePhase) {
                case pullCode: {
                    if (releaseType == ReleaseType.run) {
                        model.setOneStartTime(now);
                        model.setOneFinishTime(now);
                    } else {
                        model.setOneFinishTime(now);
                    }
                }
                break;
                case build: {
                    if (releaseType == ReleaseType.run) {
                        model.setTwoStartTime(now);
                        model.setTwoFinishTime(now);
                    } else {
                        model.setTwoFinishTime(now);
                    }
                }
                break;
                case start: {
                    if (releaseType == ReleaseType.run) {
                        model.setThreeStartTime(now);
                        model.setThreeFinishTime(now);
                    } else {
                        model.setThreeFinishTime(now);
                    }
                }
                break;
            }
            releaseServerStatusService.update(model);
            producer.billStatusChange(billModel);
        } catch (Exception e) {
            log.error(StrUtil.format("修改服务器发布状态失败 {} {}", serverModel.getIp(), billModel._getChangeBranchModel().getName()), e);
        }
    }


    @Override
    public boolean aServerRelease(ReleaseBillModel billModel, ServerModel serverModel) {
        // 异步方式
        WebThreadPool.IO_THREAD.execute(() -> {
            try {
                if (!(aServerPullCode(billModel, serverModel) && aServerBuild(billModel, serverModel) && aServerStart(billModel, serverModel))) {
                    log.warn("单机自动部署失败");
                }
            } catch (Exception e) {
                log.error(StrUtil.format("单机自动部署异常 releaseId={} ip={}", billModel.getId(), serverModel.getIp()), e);
            }
        });
        return true;
    }

    @Override
    public boolean aServerPullCode(ReleaseBillModel billModel, ServerModel serverModel) {
        // 检测项目是否存在其他的发布单正在一二阶段  一二阶段暂时互斥 不能同时存在几个部署
        setCurrentStatus(billModel, serverModel, ReleasePhase.pullCode, ReleaseType.run);
        boolean result = false;
        ShellExe shellExe = null;
        try {
            shellExe = serverService.shellExeByUsername(serverModel);
            if (releaseBillService.pullCode(billModel, shellExe)) {
                setCurrentStatus(billModel, serverModel, ReleasePhase.pullCode, ReleaseType.releaseSuccess);
                result = true;
            } else {
                setCurrentStatus(billModel, serverModel, ReleasePhase.pullCode, ReleaseType.releaseFail);
            }
        } catch (Exception e) {
            setCurrentStatus(billModel, serverModel, ReleasePhase.pullCode, ReleaseType.releaseFail);

        } finally {
            if (shellExe != null) {
                shellExe.close();
            }
        }
        return result;
    }

    @Override
    public boolean aServerBuild(ReleaseBillModel billModel, ServerModel serverModel) {
        setCurrentStatus(billModel, serverModel, ReleasePhase.build, ReleaseType.run);
        boolean result = false;
        ShellExe shellExe = null;
        try {
            shellExe = serverService.shellExeByUsername(serverModel);
            if (releaseBillService.build(billModel, shellExe)) {
                setCurrentStatus(billModel, serverModel, ReleasePhase.build, ReleaseType.releaseSuccess);
                result = true;
            } else {
                setCurrentStatus(billModel, serverModel, ReleasePhase.build, ReleaseType.releaseFail);
            }
        } catch (Exception e) {
            setCurrentStatus(billModel, serverModel, ReleasePhase.build, ReleaseType.releaseFail);
        } finally {
            if (shellExe != null) {
                shellExe.close();
            }
        }
        return result;
    }

    @Override
    public boolean aServerStart(ReleaseBillModel billModel, ServerModel serverModel) {
        setCurrentStatus(billModel, serverModel, ReleasePhase.start, ReleaseType.run);
        boolean result = false;
        ShellExe shellExe = null;
        try {
            shellExe = serverService.shellExeByUsername(serverModel);
            if (releaseBillService.startApp(billModel, shellExe)) {
                setCurrentStatus(billModel, serverModel, ReleasePhase.start, ReleaseType.releaseSuccess);
                result = true;
            } else {
                setCurrentStatus(billModel, serverModel, ReleasePhase.start, ReleaseType.releaseFail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            setCurrentStatus(billModel, serverModel, ReleasePhase.start, ReleaseType.releaseFail);
        } finally {
            if (shellExe != null) {
                shellExe.close();
            }
        }
        return result;
    }

    @Override
    public ReleaseServerStatusModel getByBillServer(ReleaseBillModel billModel, ServerModel serverModel) {
        ReleaseServerStatusModel model = releaseServerStatusJpa.findOne(
                Example.of(ReleaseServerStatusModel.builder()
                        .billId(billModel.getId())
                        .serverId(serverModel.getId())
                        .build()
                )).orElse(null);
        if (model == null) {
            model = ReleaseServerStatusModel.builder()
                    .billId(billModel.getId())
                    .serverId(serverModel.getId())
                    .build();
            releaseServerStatusService.save(model);
        }
        return model;
    }
}
