package com.nix.jingxun.addp.web.service;

import cn.hutool.core.util.StrUtil;
import com.nix.jingxun.addp.web.IEnum.ReleasePhase;
import com.nix.jingxun.addp.web.IEnum.ReleaseType;
import com.nix.jingxun.addp.web.iservice.IReleaseServerStatusService;
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
import javax.persistence.Transient;
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
        ReleaseServerStatusModel model = releaseServerStatusJpa.findOne(
                Example.of(ReleaseServerStatusModel.builder()
                        .billId(billModel.getId())
                        .serverId(serverModel.getId())
                        .build()
                )).orElse(null);

        try {
            if (model == null) {
                model = ReleaseServerStatusModel.builder()
                        .billId(billModel.getId())
                        .serverId(serverModel.getId())
                        .build();
                releaseServerStatusService.save(model);
            }
            model.setReleasePhase(releasePhase);
            model.setReleaseType(releaseType);
            switch (releasePhase) {
                case pullCode: {
                    if (releaseType == ReleaseType.run) {
                        model.setOneStartTime(LocalDateTime.now());
                    } else {
                        model.setOneFinishTime(LocalDateTime.now());
                    }
                }
                break;
                case build: {
                    if (releaseType == ReleaseType.run) {
                        model.setTwoStartTime(LocalDateTime.now());
                    } else {
                        model.setTwoFinishTime(LocalDateTime.now());
                    }
                }
                break;
                case start: {
                    if (releaseType == ReleaseType.run) {
                        model.setThreeStartTime(LocalDateTime.now());
                    } else {
                        model.setThreeFinishTime(LocalDateTime.now());
                    }
                }
                break;
            }
            releaseServerStatusService.update(model);
        } catch (Exception e) {
            log.error(StrUtil.format("修改服务器发布状态失败 {} {}", serverModel.getIp(), billModel._getChangeBranchModel().getName()), e);
        }
    }
}
