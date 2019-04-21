package com.nix.jingxun.addp.web.service;

import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.jpa.ProjectsJpa;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author keray
 * @date 2019/04/21 13:57
 */
@Service
public class ProjectsServiceImpl extends BaseServiceImpl<ProjectsModel,Long> implements IProjectsService {

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
}
