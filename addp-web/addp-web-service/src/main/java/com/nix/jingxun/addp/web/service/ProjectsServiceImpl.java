package com.nix.jingxun.addp.web.service;

import cn.hutool.core.util.StrUtil;
import com.nix.jingxun.addp.ssh.common.exception.ShellExeException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.ssh.common.util.ShellFunc;
import com.nix.jingxun.addp.ssh.common.util.ShellUtil;
import com.nix.jingxun.addp.web.common.ShellExeLog;
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
        createGitClone(projectsModel, servicesService.shellExeByUsername(servicesModel));
        return super.save(projectsModel);
    }

    public void createGitClone(ProjectsModel projectsModel, ShellExe shellExe) throws ShellExeException {
        // cd /usr/addp 没有则创建
        shellExe.syncExecute(StrUtil.format("mkdir -p {}", WebConfig.addpBaseFile), ShellExeLog.success, ShellExeLog.fail)
                // git clone gitUtl "/usr/addp/{projectName}"
                .syncExecute(StrUtil.format("git clone {} \"{}{}\"", projectsModel.getGitUrl(), WebConfig.addpBaseFile, projectsModel.getName()),
                        result -> {
                            ShellExeLog.success.accept(result, StrUtil.format("git clone {} \"{}{}\"", projectsModel.getGitUrl(), WebConfig.addpBaseFile, projectsModel.getName()));
                            // 判断git是否需要认证
                            if (ShellUtil.shellNeedKeydown(result.toString())) {
                               servicesService.gitAuth(shellExe,projectsModel);
                            }
                        },
                        error -> ShellExeLog.fail.accept(error, StrUtil.format("git clone {} \"{}{}\" fail", projectsModel.getGitUrl(), WebConfig.addpBaseFile, projectsModel.getName())))
                .close();
        // git clone成功
    }


}
