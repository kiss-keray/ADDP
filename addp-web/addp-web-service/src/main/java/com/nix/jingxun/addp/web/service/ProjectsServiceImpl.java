package com.nix.jingxun.addp.web.service;

import cn.hutool.core.util.StrUtil;
import com.nix.jingxun.addp.ssh.common.exception.ShellExeException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.ssh.common.util.ShellUtil;
import com.nix.jingxun.addp.web.common.ShellExeLog;
import com.nix.jingxun.addp.web.common.config.WebConfig;
import com.nix.jingxun.addp.web.exception.Code;
import com.nix.jingxun.addp.web.exception.WebRunException;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IServerService;
import com.nix.jingxun.addp.web.jpa.ProjectsJpa;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.relationship.jpa.ProjectsServerReJpa;
import com.nix.jingxun.addp.web.model.relationship.model.ProjectsServerRe;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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
    private ProjectsServerReJpa projectsServerReJpa;

    @Resource
    private IServerService servicesService;

    @Override
    protected JpaRepository<ProjectsModel, Long> jpa() {
        return projectsJpa;
    }

    /**
     * 新建项目时需要做的流程
     * v1：使用其他的git仓库
     * 流程链接服务器，在/opt/addp/目录下git clone仓库
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectsModel save(ProjectsModel projectsModel) throws Exception {
        boolean result = servicesService.moreServiceExec(projectsModel._getServicesModels(), servicesModel -> {
            try {
                createGitClone(projectsModel, servicesService.shellExeByUsername(servicesModel));
            } catch (Exception e) {
                throw new ShellExeException(e);
            }
        });
        if (!result) {
            throw new WebRunException(Code.exeError,"git clone 服务器组失败");
        }
        super.save(projectsModel);
        for (ProjectsServerRe re:projectsModel._getProjectsServiceRes()) {
            re.setProjectsId(projectsModel.getId());
            projectsServerReJpa.save(re);
        }
        return projectsModel;
    }

    public void createGitClone(ProjectsModel projectsModel, ShellExe shellExe) throws ShellExeException {
        // cd /usr/addp 没有则创建 如果有就删除
        if (ShellUtil.cd(StrUtil.format("{}{}",WebConfig.addpBaseFile,projectsModel.getName()),shellExe)) {
            shellExe.oneCmd(StrUtil.format("rm -rf {}{}",WebConfig.addpBaseFile,projectsModel.getName()));
        }
        ShellUtil.cd(WebConfig.addpBaseFile,shellExe);
        shellExe.syncExecute(StrUtil.format("mkdir -p {}", WebConfig.addpBaseFile), ShellExeLog.success, ShellExeLog.fail)
                // git clone gitUtl "/opt/addp/{projectName}"
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

    @Override
    public boolean cdRoot(ProjectsModel projectsModel, ShellExe shellExe) {
        // cd 到项目目录
        return ShellUtil.cd(StrUtil.format("{}{}", WebConfig.addpBaseFile, projectsModel.getName()), shellExe);
    }
}
