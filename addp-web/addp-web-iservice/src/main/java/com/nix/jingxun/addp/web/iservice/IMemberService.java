package com.nix.jingxun.addp.web.iservice;

import com.nix.jingxun.addp.web.model.MemberModel;

/**
 * @author keray
 * @date 2018/12/04 下午5:59
 */
public interface IMemberService extends BaseService<MemberModel,Long>{
    MemberModel register(MemberModel member);
    MemberModel login(String username,String password);
}
