package com.nix.jingxun.addp.web.service;

import com.nix.jingxun.addp.web.common.cache.MemberCache;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.jpa.ProjectsJpa;
import com.nix.jingxun.addp.web.model.MemberModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServicesModel;
import com.nix.jingxun.addp.web.service.base.BaseServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 新建项目时需要做的流程
     * v1：使用其他的git仓库
     * 流程链接服务器，在/user/addp/目录下git clone仓库
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectsModel save(ProjectsModel projectsModel) throws Exception {
        // 设置项目用户
        MemberModel member = MemberCache.currentUser();
        ServicesModel servicesModel = oneToOneModel(ServicesModel.class,projectsModel.getServicesId());
        // 拿到服务器执行shell
        projectsModel.setMemberId(member.getId());
        return super.save(projectsModel);
    }
}
