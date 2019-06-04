package com.nix.jingxun.addp.web.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.jcraft.jsch.JSchException;
import com.nix.jingxun.addp.ssh.common.exception.ShellExeException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.ssh.common.util.ShellUtil;
import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.common.ShellExeLog;
import com.nix.jingxun.addp.web.exception.Code;
import com.nix.jingxun.addp.web.exception.WebRunException;
import com.nix.jingxun.addp.web.iservice.IChangeBranchService;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IServerService;
import com.nix.jingxun.addp.web.jpa.ChangeBranchJpa;
import com.nix.jingxun.addp.web.model.ChangeBranchModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServerModel;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author keray
 * @date 2019/04/21 13:58
 */
@Service("changeBranchServiceImpl")
@Slf4j
public class ChangeBranchServiceImpl extends BaseServiceImpl<ChangeBranchModel, Long> implements IChangeBranchService {
    @Resource
    private ChangeBranchJpa changeBranchJpa;

    @Resource
    private IProjectsService projectsService;

    @Resource
    private IServerService servicesService;

    @Override
    protected JpaRepository<ChangeBranchModel, Long> jpa() {
        return changeBranchJpa;
    }

    /**
     * 创建变更（创建分支）
     */
    @Transactional
    @Override
    public ChangeBranchModel save(ChangeBranchModel changeBranchModel) {
        ProjectsModel projectsModel = changeBranchModel._getProjectsModel();
        List<ServerModel> serverModels = projectsModel._getServerModels();
        if (CollectionUtil.isNotEmpty(serverModels)) {
            ServerModel serverModel = serverModels.remove(0);
            try {
                ShellExe shellExe = servicesService.shellExeByUsername(serverModel);
                gitCreateBranch(changeBranchModel, shellExe);
                shellExe.close();
            } catch (JSchException | IOException e) {
                e.printStackTrace();
            }
            boolean result = servicesService.moreServiceExec(serverModels, servicesModel -> {
                try {
                    ShellExe shellExe = servicesService.shellExeByUsername(servicesModel);
                    initBranch(changeBranchModel, shellExe);
                    shellExe.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ShellExeException(e);
                }
            });
            if (!result) {
                throw new WebRunException(Code.exeError, "创建分支失败");
            }
        }
        return super.save(changeBranchModel);
    }

    public void initBranch(ChangeBranchModel changeBranchModel, ShellExe shellExe) {
        if (!projectsService.cdRoot(changeBranchModel._getProjectsModel(), shellExe)) {
            log.error("服务器{} 不存在项目{}的文件夹", shellExe.getIp(), changeBranchModel._getProjectsModel().getName());
            throw new WebRunException(Code.dataError, "服务器不存在项目文件夹");
        }
        // 先判断分支是否存在 存在直接切换
        boolean isHave = !shellExe.oneCmd("git checkout " + changeBranchModel.getBranchName()).matches("[\\S|\\s]*error:[\\S|\\s]*");
        if (!isHave) {
            shellExe.syncExecute("git fetch", ShellExeLog.success, ShellExeLog.fail)
                    .syncExecute(StrUtil.format("git checkout -b {} {}", changeBranchModel.getBranchName(), changeBranchModel._getProjectsModel().getMaster()), (r, c) -> {
                        if (r.toString().contains("error")) {
                            ShellExeLog.fail.accept(r, c);
                        }
                    }, ShellExeLog.fail)
                    .syncExecute(StrUtil.format("git branch --set-upstream-to origin/{}", changeBranchModel.getBranchName()), (r, c) -> {
                        if (r.toString().contains("error")) {
                            ShellExeLog.fail.accept(r, c);
                        }
                    }, ShellExeLog.fail);
        }
    }

    public void gitCreateBranch(ChangeBranchModel changeBranchModel, ShellExe shellExe) throws ShellExeException {
       gitCreateBranch(changeBranchModel._getProjectsModel(),changeBranchModel.getBranchName(),shellExe);
    }

    public void gitCreateBranch(ProjectsModel projectsModel,String branch, ShellExe shellExe) throws ShellExeException {
        if (!projectsService.cdRoot(projectsModel, shellExe)) {
            log.error("服务器{} 不存在项目{}的文件夹", shellExe.getIp(), projectsModel.getName());
            throw new WebRunException(Code.dataError, "服务器不存在项目文件夹");
        }
        // 先判断分支是否存在 存在直接切换
        boolean isHave = !shellExe.oneCmd("git checkout " + branch).matches("[\\S|\\s]*error:[\\S|\\s]*");
        if (!isHave) {
            // 先切换到master分支
            shellExe.syncExecute(StrUtil.format("git checkout {}", projectsModel.getMaster()),
                    ShellExeLog.success, ShellExeLog.fail)
                    // 创建本地分支 git branch xxx
                    .syncExecute(StrUtil.format("git checkout -b {}", branch), ShellExeLog.success, ShellExeLog.fail)
                    //push 到远程分支
                    .syncExecute(StrUtil.format("git push origin {}", branch), (r, c) -> {
                        ShellExeLog.success.accept(r, c);
                        // 如果push需要验证
                        if (ShellUtil.shellNeedKeydown(r.toString())) {
                            servicesService.gitAuth(shellExe, projectsModel);
                        }
                    }, ShellExeLog.fail)
                    // 关联分支
                    .syncExecute(StrUtil.format("git branch --set-upstream-to origin/{}", branch),
                            (r, c) -> {
                                if (r.toString().matches("[\\S|\\s]*error:[\\S|\\s]*")) {
                                    ShellExeLog.fail.accept(r, c);
                                    return;
                                }
                                ShellExeLog.success.accept(r, c);
                            }, ShellExeLog.fail);
        }
    }

    @Override
    public List<ChangeBranchModel> projectChanges(Long projectId) {
        return jpa().findAll(Example.of(ChangeBranchModel.builder().projectId(projectId).build()));
    }

    @Override
    public boolean branchIsNew(ChangeBranchModel model, ADDPEnvironment environment) {
        List<ServerModel> serverModels = servicesService.selectAllServes(model._getProjectsModel(), environment);
        if (CollectionUtil.isEmpty(serverModels)) {
            return false;
        }
        AtomicBoolean result = new AtomicBoolean(false);
        try {
            ShellExe shellExe = servicesService.shellExeByUsername(serverModels.get(0));
            projectsService.cdRoot(model._getProjectsModel(), shellExe);
            shellExe.syncExecute("git checkout .", ShellExeLog.success, ShellExeLog.fail)
                    .syncExecute(StrUtil.format("git checkout {}", model.getBranchName()), ShellExeLog.success, ShellExeLog.fail)
                    .syncExecute("git fetch", (r, c) -> {
                        ShellExeLog.success.accept(r, c);
                        if (ShellUtil.shellNeedKeydown(r.toString())) {
                            servicesService.gitAuth(shellExe, model._getProjectsModel());
                        }
                    }, ShellExeLog.fail)
                    .syncExecute(StrUtil.format("git diff {} origin/{}", model.getBranchName(), model.getBranchName()), (r, c) -> {
                        ShellExeLog.success.accept(r, c);
                        if (r.toString().contains("diff --git")) {
                            result.set(true);
                        }
                    }, ShellExeLog.fail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.get();
    }
}
