package com.nix.jingxun.addp.web.start.controller;

import com.nix.jingxun.addp.common.Result;
import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.common.cache.MemberCache;
import com.nix.jingxun.addp.web.common.util.AESUtil;
import com.nix.jingxun.addp.web.domain.WebPageable;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IServerService;
import com.nix.jingxun.addp.web.model.MemberModel;
import com.nix.jingxun.addp.web.model.ServerModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
/**
 * @author keray
 * @date 2019/04/21 17:26
 */
@RestController
@RequestMapping("/server")
@Api("服务器管理api")
public class ServerController extends BaseController{

    @Resource
    private IServerService servicesService;
    @Resource
    private IProjectsService projectsService;

    @PostMapping("/create")
    public Result create(@Valid @ModelAttribute ServerModel serverModel) {
        return Result.of(() -> {
            MemberModel currentMember = MemberCache.currentUser();
            serverModel.setMemberId(currentMember == null ? -1 : currentMember.getId());
            serverModel.setPassword(AESUtil.encryption(serverModel.getPassword()));
            try {
                return servicesService.save(serverModel);
            } catch (Exception e) {
                e.printStackTrace();
                return Result.fail(e);
            }

        }).logFail();
    }

    @PutMapping("/update")
    public Result update(@Valid @ModelAttribute ServerModel serverModel) {
        return Result.of(() -> {
            serverModel.setPassword(AESUtil.encryption(serverModel.getPassword()));
            try {
                ServerModel newM = servicesService.update(serverModel);
                newM.setPassword(AESUtil.decrypt(newM.getPassword()));
                return newM;
            } catch (Exception e) {
                return e;
            }
        }).failFlat(this::failFlat).logFail();
    }

    @PostMapping("/list")
    public Result list(@ModelAttribute WebPageable webPageable, @RequestParam(value = "env",required = false) ADDPEnvironment environment) {
        return Result.of(() -> {
            if (webPageable != null) {
                Page<ServerModel> page = servicesService.memberServices(webPageable,environment);
                page.getContent().forEach(s -> s.setPassword(AESUtil.decrypt(s.getPassword())));
                return page;
            }
            return Collections.emptyList();
        }).failFlat(this::failFlat).logFail();
    }
    @ApiOperation("获取项目全部的服务器")
    @GetMapping("/psList/{pId}")
    public Result psList(@PathVariable Long pId) {
        return Result.of(() -> projectsService.findById(pId)._getServicesModels()).failFlat(this::failFlat).logFail();
    }

}
