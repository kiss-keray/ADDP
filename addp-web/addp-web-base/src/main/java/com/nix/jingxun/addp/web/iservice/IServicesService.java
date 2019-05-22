package com.nix.jingxun.addp.web.iservice;

import com.jcraft.jsch.JSchException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.web.diamond.ADDPEnvironment;
import com.nix.jingxun.addp.web.model.MemberModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServicesModel;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author keray
 * @date 2019/04/21 13:53
 */
public interface IServicesService extends BaseService<ServicesModel,Long> {

    List<ServicesModel> selectMemberServices(MemberModel memberModel);
    /**
     * 服务器shell链接
     * */
    ShellExe shellExeByUsername(ServicesModel servicesModel) throws IOException, JSchException;
    /**
     * git认证
     * */
    ShellExe gitAuth(ShellExe shellExe, ProjectsModel projectsModel);

    /**
     * 服务器组执行
     * */
    boolean moreServiceExec(List<ServicesModel> servicesModels, Consumer<ServicesModel> exec);

    /**
     * 根据环境得到项目的服务器组
     * 如果是正式环境机器，在获取备份环境机器
     * */
    List<ServicesModel> selectEnvServices(ProjectsModel projectsModel, ADDPEnvironment environment);

    /**
     * 将生产服务器更改问允许发布状态
     * */
    void updateProAllow(List<Long> ids) throws Exception;

}
