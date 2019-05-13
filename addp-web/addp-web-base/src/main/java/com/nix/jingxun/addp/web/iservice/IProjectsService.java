package com.nix.jingxun.addp.web.iservice;

import com.nix.jingxun.addp.ssh.common.exception.ShellExeException;
import com.nix.jingxun.addp.ssh.common.util.ShellExe;
import com.nix.jingxun.addp.web.model.ProjectsModel;

/**
 * @author keray
 * @date 2019/04/21 13:54
 */
public interface IProjectsService extends BaseService<ProjectsModel,Long> {
    void createGitClone(ProjectsModel projectsModel, ShellExe shellExe) throws ShellExeException;
}
