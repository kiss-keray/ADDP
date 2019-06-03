package com.nix.jingxun.addp.web.iservice;

import com.jcraft.jsch.JSchException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.domain.WebPageable;
import com.nix.jingxun.addp.web.model.MemberModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServerModel;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author keray
 * @date 2019/04/21 13:53
 */
public interface IServerService extends BaseService<ServerModel,Long> {

    List<ServerModel> selectMemberServices(MemberModel memberModel);
    /**
     * 服务器shell链接
     * */
    ShellExe shellExeByUsername(ServerModel serverModel) throws JSchException,IOException;
    /**
     * git认证
     * */
    ShellExe gitAuth(ShellExe shellExe, ProjectsModel projectsModel);

    /**
     * 服务器组执行
     * */
    boolean moreServiceExec(List<ServerModel> serverModels, Consumer<ServerModel> exec);

    /**
     * 根据环境得到项目的服务器组
     * 如果是正式环境机器，在获取备份环境机器
     * */
    List<ServerModel> selectEnvAllowServer(ProjectsModel projectsModel, ADDPEnvironment environment);

    List<ServerModel> selectAllServes(ProjectsModel projectsModel, ADDPEnvironment environment);

    /**
     * 将生产服务器更改问允许发布状态
     * */
    void updateProAllow(List<Long> ids) throws Exception;

    /**
     * 获取当前用户的服务器
     * */
    Page<ServerModel> memberServices(WebPageable webPageable, ADDPEnvironment environment);

}
