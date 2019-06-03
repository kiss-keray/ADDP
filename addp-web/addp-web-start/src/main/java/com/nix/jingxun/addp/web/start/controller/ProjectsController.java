package com.nix.jingxun.addp.web.start.controller;

import com.nix.jingxun.addp.common.Result;
import com.nix.jingxun.addp.ssh.common.exception.ShellConnectException;
import com.nix.jingxun.addp.web.common.cache.MemberCache;
import com.nix.jingxun.addp.web.common.util.AESUtil;
import com.nix.jingxun.addp.web.domain.WebPageable;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IServerService;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;

/**
 * @author keray
 * @date 2019/04/21 17:40
 */
@RestController
@RequestMapping("/project")
public class ProjectsController  extends BaseController{

    @Resource
    private IProjectsService projectsService;

    @Resource
    private IServerService servicesService;

    @PostMapping("/create")
    public Result create(@Valid @RequestBody ProjectsModel projectsModel) {
        return Result.of(() -> {
            try {
                if (!projectsModel._getServerModels().stream().allMatch(servicesModel -> servicesModel.getMemberId().equals(MemberCache.currentUser().getId()))) {
                    return Result.fail("1401", "no project permission " + projectsModel.getName());
                }
                projectsModel.setMemberId(MemberCache.currentUser().getId());
                return projectsService.save(projectsModel);
            } catch (ShellConnectException e) {
                return Result.fail("1404","服务器连接失败");
            } catch (Exception e) {
                e.printStackTrace();
                return Result.fail(e);
            }
        }).logFail();
    }

    @PostMapping("/list")
    public Result list(@ModelAttribute WebPageable webPageable) {
        return Result.of(() -> {
            if (webPageable != null) {
                Page<ProjectsModel> page = projectsService.page(webPageable,
                        Example.of(ProjectsModel.builder()
                                .memberId(MemberCache.currentUser().getId())
                                .build()));
                page.getContent().forEach(p -> p.setGitPassword(AESUtil.decrypt(p.getGitPassword())));
                return page;
            }
            return Collections.emptyList();
        }).failFlat(this::failFlat).logFail();
    }

    @PostMapping("/update")
    public Result update(@Valid @RequestBody ProjectsModel model) {
        return Result.of(() -> {
            model.setGitPassword(AESUtil.encryption(model.getGitPassword()));
            try {
                ProjectsModel newM = projectsService.update(model);
                newM.setGitPassword(AESUtil.decrypt(newM.getGitPassword()));
                return newM;
            } catch (Exception e) {
                return e;
            }
        }).failFlat(this::failFlat).logFail();
    }

}
