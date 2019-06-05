package com.nix.jingxun.addp.web.iservice;

import com.nix.jingxun.addp.web.model.ReleaseBillModel;

/**
 * @author keray
 * @date 2019/06/05 20:47
 */
public interface IWebSocket {
    void notifyClient(ReleaseBillModel billModel);
}
