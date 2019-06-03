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
     * 设置当前状态
     * */
    void setCurrentStatus(ReleaseBillModel billModel, ServerModel serverModel, ReleasePhase releasePhase, ReleaseType releaseType);
}
