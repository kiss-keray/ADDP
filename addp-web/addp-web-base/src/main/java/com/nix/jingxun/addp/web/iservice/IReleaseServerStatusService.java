package com.nix.jingxun.addp.web.iservice;

import com.nix.jingxun.addp.web.IEnum.ReleasePhase;
import com.nix.jingxun.addp.web.IEnum.ReleaseType;
import com.nix.jingxun.addp.web.model.ReleaseBillModel;
import com.nix.jingxun.addp.web.model.ReleaseServerStatusModel;
import com.nix.jingxun.addp.web.model.ServerModel;

import java.util.List;

/**
 * @author keray
 * @date 2019/06/03 12:09
 */
public interface IReleaseServerStatusService extends BaseService<ReleaseServerStatusModel,Long> {

    List<ReleaseServerStatusModel> selectBillAllStatus(ReleaseBillModel releaseBillModel);

    /**
     * 设置服务器当前部署当前状态
     * */
    void setCurrentStatus(ReleaseBillModel billModel, ServerModel serverModel, ReleasePhase releasePhase, ReleaseType releaseType);

    /**
     * 单个服务器自动重新部署
     * 按流程执行123阶段
     * */
    boolean aServerRelease(ReleaseBillModel billModel,ServerModel serverModel);

    /**
     * 单服务器一阶段执行一阶段
     * 区分线上环境，线上环境需要切换到线上分支
     * */
    boolean aServerPullCode(ReleaseBillModel billModel,ServerModel serverModel);

    /**
     * 单服务器二阶段
     * 不区分环境
     * */
    boolean aServerBuild(ReleaseBillModel billModel,ServerModel serverModel);

    /**
     * 单服务器第三阶段
     * 不区分环境，线上也不会有暂停
     * */
    boolean aServerStart(ReleaseBillModel billModel,ServerModel serverModel);

    ReleaseServerStatusModel getByBillServer(ReleaseBillModel billModel, ServerModel serverModel);
}
