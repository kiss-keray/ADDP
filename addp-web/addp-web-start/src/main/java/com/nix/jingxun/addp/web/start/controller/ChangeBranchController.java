package com.nix.jingxun.addp.web.start.controller;

import com.nix.jingxun.addp.web.iservice.IChangeBranchService;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.model.ChangeBranchModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServicesModel;
import com.nix.jingxun.addp.web.start.common.Result;
import com.nix.jingxun.addp.web.start.common.cache.MemberCache;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author keray
 * @date 2019/04/21 17:52
 */
@RestController
@RequestMapping("/change")
public class ChangeBranchController {

    @Resource
    private IChangeBranchService changeBranchService;
    @Resource
    private IProjectsService projectsService;

    @PostMapping("/create")
    public Result create(@Valid @ModelAttribute ChangeBranchModel changeBranchModel) {
        return Result.of(() -> {
            try {
                ProjectsModel projectsModel = projectsService.findById(changeBranchModel.getProjectId());
                if (!MemberCache.currentUser().getId().equals(
                        projectsService.oneToOneModel(ServicesModel.class,projectsModel.getServicesId()).getMemberId())
                ) {
                    return Result.fail("1401","no project permission " + projectsModel.getName());
                }
                return changeBranchService.save(changeBranchModel);
            } catch (Exception e) {
                e.printStackTrace();
                return Result.fail(e);
            }
        }).logFail();
    }
}
