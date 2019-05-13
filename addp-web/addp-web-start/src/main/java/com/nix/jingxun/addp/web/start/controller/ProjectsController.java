package com.nix.jingxun.addp.web.start.controller;

import com.nix.jingxun.addp.common.Result;
import com.nix.jingxun.addp.ssh.common.exception.ShellConnectException;
import com.nix.jingxun.addp.web.common.cache.MemberCache;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IServicesService;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServicesModel;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author keray
 * @date 2019/04/21 17:40
 */
@RestController
@RequestMapping("/projects")
public class ProjectsController  extends BaseController{

    @Resource
    private IProjectsService projectsService;

    @Resource
    private IServicesService servicesService;

    @PostMapping("/create")
    public Result create(@Valid @ModelAttribute ProjectsModel projectsModel) {
        return Result.of(() -> {
            try {
                ServicesModel servicesModel = servicesService.findById(projectsModel.getServicesId());
                if (servicesModel == null) {
                    return Result.fail("1404","服务器不存在");
                }
                if (!MemberCache.currentUser().getId().equals(servicesModel.getMemberId())) {
                    return Result.fail("1401","no permission:" + projectsModel.getServicesId());
                }
                projectsModel.setMemberId(servicesModel.getMemberId());
                projectsModel.setServicesModel(servicesModel);
                return projectsService.save(projectsModel);
            } catch (ShellConnectException e) {
                return Result.fail("1404","服务器连接失败");
            } catch (Exception e) {
                e.printStackTrace();
                return Result.fail(e);
            }
        }).logFail();
    }
}
