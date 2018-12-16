package com.nix.jingxun.addp.rpc.producer;

import org.springframework.stereotype.Component;

/**
 * @author keray
 * @date 2018/12/15 15:58
 */
@Component
public interface RPCInvoke {
    Object invoke(String sign,Object[] args) throws Exception;
}
