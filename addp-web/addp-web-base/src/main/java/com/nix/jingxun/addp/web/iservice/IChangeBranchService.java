package com.nix.jingxun.addp.web.iservice;

import com.nix.jingxun.addp.ssh.common.exception.ShellExeException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.web.model.ChangeBranchModel;

/**
 * @author keray
 * @date 2019/04/21 13:54
 */
public interface IChangeBranchService extends BaseService<ChangeBranchModel,Long> {
    /**
     * 创建分支
     * */
    void gitCreateBranch(ChangeBranchModel changeBranchModel, ShellExe shellExe) throws ShellExeException;

    /**
     * 部署变更
     * 暂时支持单一变更部署
     * 部署变更流程
     * */
     void deployBranch(ChangeBranchModel changeBranchModel) throws Exception;
}
