package com.nix.jingxun.addp.web.service;

import cn.hutool.core.util.StrUtil;
import com.nix.jingxun.addp.ssh.common.exception.ShellExeException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.ssh.common.util.ShellUtil;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.jpa.ProjectsJpa;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServicesModel;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.security.auth.message.AuthException;

/**
 * @author keray
 * @date 2019/04/21 13:57
 */
@Service
@Slf4j
public class ProjectsServiceImpl extends BaseServiceImpl<ProjectsModel, Long> implements IProjectsService {

    @Resource
    private ProjectsJpa projectsJpa;

    @Override
    protected JpaRepository<ProjectsModel, Long> jpa() {
        return projectsJpa;
    }

    @Override
    protected Class<ProjectsModel> modelType() {
        return ProjectsModel.class;
    }

    /**
     * 新建项目时需要做的流程
     * v1：使用其他的git仓库
     * 流程链接服务器，在/user/addp/目录下git clone仓库
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectsModel save(ProjectsModel projectsModel) throws Exception {
        ServicesModel servicesModel = projectsModel.getServicesModel();
        // 拿到服务器执行shell
        ShellExe shellExe = shellExeByUsername(servicesModel);
        final ShellFunc<Object> fail = (error, msg) -> {
            Exception e = (Exception) error;
            log.error(msg, error);
            throw new ShellExeException(msg, e);
        };
        final ShellFunc<Object> success = (result, msg) -> {
            if (!ShellUtil.commandIsExec(result.toString())) {
                fail.accept(null, StrUtil.format("{} 命令未找到", msg));
                return;
            }
            log.info("{}shell:{}{}{}", System.lineSeparator(), msg, System.lineSeparator(), result);
        };
        // cd /usr/addp 没有则创建
        shellExe.syncExecute("mkdir -p /usr/addp/",
                result -> success.accept(result, "mkdir -p /usr/addp/"),
                error -> fail.accept(error, "mkdir -p /usr/addp/ fail..."))
                // git clone gitUtl "/usr/addp/{projectName}"
                .syncExecute(StrUtil.format("git clone {} \"/usr/addp/{}\"", projectsModel.getGitUrl(), projectsModel.getName()),
                        result -> {
                            success.accept(result, StrUtil.format("git clone {} \"/usr/addp/{}\"", projectsModel.getGitUrl(), projectsModel.getName()));
                            // 判断git是否需要认证
                            if (ShellUtil.commandIsExec(result.toString())) {
                                //输入账号
                                shellExe.syncExecute(projectsModel.getGitUsername(),
                                        result1 -> success.accept(result, projectsModel.getGitUsername()),
                                        error -> fail.accept(error, "输入账号异常"))
                                        //输入密码
                                        .syncExecute(projectsModel.getGitPassword(),
                                                result1 -> {
                                                    // 判断shell返回的认证信息；
                                                    if (result1.toString().contains("Authentication failed")) {
                                                        fail.accept(new AuthException(StrUtil.format("fatal: Authentication failed for '{}'", projectsModel.getGitUrl())),
                                                                "git密码验证失败");
                                                    }
                                                },
                                                error -> fail.accept(error, "密码输入执行异常"));
                            }
                        },
                        error -> fail.accept(error, StrUtil.format("git clone {} \"/usr/addp/{}\"", projectsModel.getGitUrl(), projectsModel.getName())));
        // git clone成功
        return super.save(projectsModel);
    }

    private ShellExe shellExeByUsername(ServicesModel servicesModel) throws Exception {
        return ShellExe.connect(servicesModel.getIp(), servicesModel.getUsername(), servicesModel.getPassword());
    }


}

interface ShellFunc<T> {
    void accept(T result, String msg);
}