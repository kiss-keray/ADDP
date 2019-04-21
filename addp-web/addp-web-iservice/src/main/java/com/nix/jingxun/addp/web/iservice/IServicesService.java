package com.nix.jingxun.addp.web.iservice;

import com.nix.jingxun.addp.web.model.MemberModel;
import com.nix.jingxun.addp.web.model.ServicesModel;

import java.util.List;

/**
 * @author keray
 * @date 2019/04/21 13:53
 */
public interface IServicesService extends BaseService<ServicesModel,Long>{

    List<ServicesModel> selectMemberServices(MemberModel memberModel);


}
