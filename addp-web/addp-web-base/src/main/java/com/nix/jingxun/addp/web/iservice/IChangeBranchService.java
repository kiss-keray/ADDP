package com.nix.jingxun.addp.web.iservice;

import com.nix.jingxun.addp.ssh.common.exception.ShellExeException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.web.model.ChangeBranchModel;

import java.util.List;

/**
 * @author keray
 * @date 2019/04/21 13:54
 */
public interface IChangeBranchService extends BaseService<ChangeBranchModel, Long> {

    /**
     * 初始化分支 当确定远程分支一定存在是使用
     * */
    void initBranch(ChangeBranchModel changeBranchModel, ShellExe shellExe);
    /**
     * 创建分支，当远程分支可能不存在时使用
     */
    void gitCreateBranch(ChangeBranchModel changeBranchModel, ShellExe shellExe) throws ShellExeException;

    /**
     * 拉取项目的全部变更
     * */
    List<ChangeBranchModel> projectChanges(Long projectId);

}
