package com.nix.jingxun.addp.web.start.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.nix.jingxun.addp.common.Result;
import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.common.cache.MemberCache;
import com.nix.jingxun.addp.web.domain.WebPageable;
import com.nix.jingxun.addp.web.exception.Code;
import com.nix.jingxun.addp.web.iservice.IChangeBranchService;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IReleaseBillService;
import com.nix.jingxun.addp.web.model.ChangeBranchModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServerModel;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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

    @Resource
    private IReleaseBillService releaseBillService;

    @PostMapping("/create")
    public Result create(@Valid @ModelAttribute ChangeBranchModel changeBranchModel) {
        return Result.of(() -> {
            ProjectsModel projectsModel = projectsService.findById(changeBranchModel.getProjectId());
            if (!projectsModel._getServerModels().stream().allMatch(servicesModel -> servicesModel.getMemberId().equals(MemberCache.currentUser().getId()))) {
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
    public Result list(@ModelAttribute WebPageable webPageable, @RequestParam("projectId") Long projectId) {
        return Result.of(() -> {
            Page<ChangeBranchModel> page = changeBranchService.page(webPageable);
            page.getContent().forEach(model -> model.setProjectsModel(model._getProjectsModel()));
            return page;
        }).failFlat(this::failFlat).logFail();
    }

    @GetMapping("/projectChanges/{projectId}")
    public Result list(@PathVariable Long projectId) {
        return Result.of(() -> {
            List<ChangeBranchModel> models = changeBranchService.projectChanges(projectId);
            models.forEach(c -> c.setProjectsModel(models.get(0)._getProjectsModel()));
            return models;
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

    @GetMapping("/createBill")
    public Result changeRelease(@RequestParam("id")Long id ,@RequestParam("env") ADDPEnvironment environment) {
        return Result.of(() -> {
            try {
                ChangeBranchModel model = changeBranchService.findById(id);
                List<ServerModel> servers = model._getProjectsModel()._getServerModels();
                if (CollectionUtil.isEmpty(servers) || CollectionUtil.isEmpty(servers.stream()
                .filter(s -> s.getEnvironment() == environment).collect(Collectors.toList()))) {
                    return Result.fail(Code.dataError.name(),"当前环境未设置服务器，无法部署");
                }
                return releaseBillService.createBill(model,environment);
            } catch (Exception e) {
                return e;
            }
        }).failFlat(this::failFlat).logFail();
    }

    /**
     * 检测代码是否有新提交
     * */
    @GetMapping("/checkBranch")
    public Result branchIsNew(@RequestParam("id")Long id) {
        return Result.of(() -> changeBranchService.branchIsNew(changeBranchService.findById(id))).failFlat(this::failFlat).logFail();
    }
}
