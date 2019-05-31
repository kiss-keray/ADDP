package com.nix.jingxun.addp.web.start.controller;

import com.nix.jingxun.addp.common.Result;
import com.nix.jingxun.addp.web.common.cache.MemberCache;
import com.nix.jingxun.addp.web.common.util.AESUtil;
import com.nix.jingxun.addp.web.domain.WebPageable;
import com.nix.jingxun.addp.web.iservice.IChangeBranchService;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.model.ChangeBranchModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServerModel;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author keray
 * @date 2019/04/21 17:52
 */
@RestController
@RequestMapping("/change")
public class ChangeBranchController extends BaseController {

    @Resource
    private IChangeBranchService changeBranchService;
    @Resource
    private IProjectsService projectsService;

    @PostMapping("/create")
    public Result create(@Valid @ModelAttribute ChangeBranchModel changeBranchModel) {
        return Result.of(() -> {
            ProjectsModel projectsModel = projectsService.findById(changeBranchModel.getProjectId());
            if (!projectsModel._getServicesModels().stream().allMatch(servicesModel -> servicesModel.getMemberId().equals(MemberCache.currentUser().getId()))) {
                return Result.fail("1401", "no project permission " + projectsModel.getName());
            }
            try {
                return changeBranchService.save(changeBranchModel);
            } catch (Exception e) {
                return e;
            }
        }).failFlat(this::failFlat).logFail();
    }

    @PostMapping("/start")
    public Result start(@RequestParam("id") Long id) {
        return Result.of(() -> {
//            ChangeBranchModel changeBranchModel =
            return null;
        }).failFlat(this::failFlat).logFail();
    }

    @PostMapping("/list")
    public Result list(@ModelAttribute WebPageable webPageable) {
        return Result.of(() -> {
            Page<ChangeBranchModel> page = changeBranchService.page(webPageable);
            page.getContent().forEach(model -> model.setProjectsModel(model._getProjectsModel()));
            return page;
        }).failFlat(this::failFlat).logFail();
    }
    @PutMapping("/update")
    public Result update(@Valid @ModelAttribute ChangeBranchModel model) {
        return Result.of(() -> {
            try {
                return changeBranchService.update(model);
            } catch (Exception e) {
                return e;
            }
        }).failFlat(this::failFlat).logFail();
    }
}
