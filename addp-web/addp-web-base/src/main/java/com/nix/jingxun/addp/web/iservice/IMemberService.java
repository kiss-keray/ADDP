package com.nix.jingxun.addp.web.iservice;

import com.nix.jingxun.addp.rpc.common.RPCInterfaceAnnotation;
import com.nix.jingxun.addp.web.model.MemberModel;

/**
 * @author keray
 * @date 2018/12/04 下午5:59
 */
@RPCInterfaceAnnotation(appName = "app1")
public interface IMemberService extends BaseService<MemberModel,Long> {
    MemberModel register(MemberModel member);
    MemberModel login(String username,String password);
    MemberModel add(String username,String password);
}
