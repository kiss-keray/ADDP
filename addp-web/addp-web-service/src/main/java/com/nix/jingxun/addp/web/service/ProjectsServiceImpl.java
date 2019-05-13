package com.nix.jingxun.addp.web.service;

import cn.hutool.core.util.StrUtil;
import com.nix.jingxun.addp.ssh.common.exception.ShellExeException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.ssh.common.util.ShellUtil;
import com.nix.jingxun.addp.web.common.config.WebConfig;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IServicesService;
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

    @Resource
    private IServicesService servicesService;

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
        createGitClone(projectsModel,servicesService.shellExeByUsername(servicesModel));
        return super.save(projectsModel);
    }

    public void createGitClone(ProjectsModel projectsModel,ShellExe shellExe) throws ShellExeException {
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
        shellExe.syncExecute(StrUtil.format("mkdir -p {}", WebConfig.addpBaseFile),
                result -> success.accept(result, StrUtil.format("mkdir -p {}", WebConfig.addpBaseFile)),
                error -> fail.accept(error, StrUtil.format("mkdir -p {} fail", WebConfig.addpBaseFile)))
                // git clone gitUtl "/usr/addp/{projectName}"
                .syncExecute(StrUtil.format("git clone {} \"{}{}\"", projectsModel.getGitUrl(),WebConfig.addpBaseFile, projectsModel.getName()),
                        result -> {
                            success.accept(result, StrUtil.format("git clone {} \"{}{}\"", projectsModel.getGitUrl(),WebConfig.addpBaseFile, projectsModel.getName()));
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
                        error -> fail.accept(error, StrUtil.format("git clone {} \"{}{}\" fail", projectsModel.getGitUrl(),WebConfig.addpBaseFile, projectsModel.getName())))
                .close();
        // git clone成功
    }



}

interface ShellFunc<T> {
    void accept(T result, String msg);
}