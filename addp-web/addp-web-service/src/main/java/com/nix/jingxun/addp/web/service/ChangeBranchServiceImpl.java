package com.nix.jingxun.addp.web.service;

import cn.hutool.core.util.StrUtil;
import com.nix.jingxun.addp.ssh.common.exception.ShellExeException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.ssh.common.util.ShellUtil;
import com.nix.jingxun.addp.web.common.ShellExeLog;
import com.nix.jingxun.addp.web.common.config.WebConfig;
import com.nix.jingxun.addp.web.exception.Code;
import com.nix.jingxun.addp.web.exception.WebRunException;
import com.nix.jingxun.addp.web.iservice.IChangeBranchService;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IServicesService;
import com.nix.jingxun.addp.web.jpa.ChangeBranchJpa;
import com.nix.jingxun.addp.web.model.ChangeBranchModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServicesModel;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author keray
 * @date 2019/04/21 13:58
 */
@Service("changeBranchServiceImpl")
public class ChangeBranchServiceImpl extends BaseServiceImpl<ChangeBranchModel, Long> implements IChangeBranchService {
    @Resource
    private ChangeBranchJpa changeBranchJpa;

    @Resource
    private IProjectsService projectsService;

    @Resource
    private IServicesService servicesService;

    @Override
    protected JpaRepository<ChangeBranchModel, Long> jpa() {
        return changeBranchJpa;
    }

    @Override
    protected Class<ChangeBranchModel> modelType() {
        return ChangeBranchModel.class;
    }

    /**
     * 创建变更（创建分支）
     */
    @Override
    public ChangeBranchModel save(ChangeBranchModel changeBranchModel) throws Exception {
        ProjectsModel projectsModel = changeBranchModel.getProjectsModel();
        ServicesModel servicesModel = projectsModel.getServicesModel();
        ShellExe shellExe = servicesService.shellExeByUsername(servicesModel);
        // cd到项目目录
        if (!ShellUtil.cd(WebConfig.addpBaseFile + projectsModel.getName(), shellExe)) {
            throw new WebRunException(Code.dataError, WebConfig.addpBaseFile + projectsModel.getName() + " 文件夹不存在");
        }
        gitCreateBranch(changeBranchModel, shellExe);
        return super.save(changeBranchModel);
    }

    public void gitCreateBranch(ChangeBranchModel changeBranchModel, ShellExe shellExe) throws ShellExeException {
        // 先切换到master分支
        shellExe.syncExecute("git chechou master", ShellExeLog.success, ShellExeLog.fail)
                // 创建本地分支 git branch xxx
                .syncExecute(StrUtil.format("git branch {}", changeBranchModel.getBranchName()), ShellExeLog.success, ShellExeLog.fail)
                // 切换分支 git checkout xxx
                .syncExecute(StrUtil.format("git checkout {}", changeBranchModel.getBranchName()), (r, c) -> {
                    // 切换分支没有返回Switched to branch 'xxx'表示失败
                    if (!r.toString().contains("Switched to branch")) {
                        ShellExeLog.fail.accept(r, c);
                    }
                    ShellExeLog.success.accept(r, c);
                }, ShellExeLog.fail)
                //push 到远程分支
                .syncExecute(StrUtil.format("git push origin {}", changeBranchModel.getBranchName()), (r, c) -> {
                    ShellExeLog.success.accept(r, c);
                    // 如果push需要验证
                    if (ShellUtil.shellNeedKeydown(r.toString())) {
                        servicesService.gitAuth(shellExe, changeBranchModel.getProjectsModel());
                    }
                }, ShellExeLog.fail);
        // push 远程分支完成
    }
}
