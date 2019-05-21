package com.nix.jingxun.addp.web.service;

import cn.hutool.core.util.StrUtil;
import com.nix.jingxun.addp.ssh.common.exception.ShellExeException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.ssh.common.util.ShellUtil;
import com.nix.jingxun.addp.web.common.ShellExeLog;
import com.nix.jingxun.addp.web.exception.Code;
import com.nix.jingxun.addp.web.exception.WebRunException;
import com.nix.jingxun.addp.web.iservice.IChangeBranchService;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IServicesService;
import com.nix.jingxun.addp.web.jpa.ChangeBranchJpa;
import com.nix.jingxun.addp.web.model.ChangeBranchModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    private IServicesService servicesService;

    @Override
    protected JpaRepository<ChangeBranchModel, Long> jpa() {
        return changeBranchJpa;
    }

    /**
     * 创建变更（创建分支）
     */
    @Override
    public ChangeBranchModel save(ChangeBranchModel changeBranchModel) throws Exception {
        ProjectsModel projectsModel = changeBranchModel._getProjectsModel();
        boolean result = servicesService.moreServiceExec(projectsModel.getServicesModels(), servicesModel -> {
            try {
                gitCreateBranch(changeBranchModel, servicesService.shellExeByUsername(servicesModel));
            } catch (Exception e) {
                throw new ShellExeException(e);
            }
        });
        if (!result) {
            throw new WebRunException(Code.exeError, "创建分支失败");
        }
        return super.save(changeBranchModel);
    }

    public void gitCreateBranch(ChangeBranchModel changeBranchModel, ShellExe shellExe) throws ShellExeException {
        if (!projectsService.cdRoot(changeBranchModel._getProjectsModel(), shellExe)) {
            log.error("服务器{} 不存在项目{}的文件夹", shellExe.getIp(), changeBranchModel._getProjectsModel().getName());
            throw new WebRunException(Code.dataError, "服务器不存在项目文件夹");
        }
        // 先判断分支是否存在 存在直接切换
        boolean isHave = !shellExe.oneCmd("git checkout " + changeBranchModel.getBranchName()).matches("[\\S|\\s]*error:[\\S|\\s]*");
        if (!isHave) {
            // 先切换到master分支
            shellExe.syncExecute("git checkout master", ShellExeLog.success, ShellExeLog.fail)
                    // 创建本地分支 git branch xxx
                    .syncExecute(StrUtil.format("git branch {}", changeBranchModel.getBranchName()), ShellExeLog.success, ShellExeLog.fail);
        }
        // 切换分支 git checkout xxx
        shellExe.syncExecute(StrUtil.format("git checkout {}", changeBranchModel.getBranchName()), (r, c) -> {
            // 切换分支没有返回Switched to branch 'xxx' || Already on 'xxxx'表示失败
            if (!r.toString().contains("Switched to branch") && !r.toString().contains("Already")) {
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
                }, ShellExeLog.fail)
                // 关联分支
                .syncExecute(StrUtil.format("git branch --set-upstream {} origin/{}", changeBranchModel.getBranchName(), changeBranchModel.getBranchName()),
                        (r, c) -> {
                            if (r.toString().matches("[\\S|\\s]*error:[\\S|\\s]*")) {
                                ShellExeLog.fail.accept(r, c);
                                return;
                            }
                            ShellExeLog.success.accept(r, c);
                        })
                .close();
    }

}
