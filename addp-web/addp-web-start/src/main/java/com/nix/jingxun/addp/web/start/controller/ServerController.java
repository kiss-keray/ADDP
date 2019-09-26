package com.nix.jingxun.addp.web.start.controller;

import com.nix.jingxun.addp.web.common.Result;
import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.common.annotation.Clear;
import com.nix.jingxun.addp.web.common.cache.MemberCache;
import com.nix.jingxun.addp.web.common.util.AESUtil;
import com.nix.jingxun.addp.web.domain.WebPageable;
import com.nix.jingxun.addp.web.exception.Code;
import com.nix.jingxun.addp.web.exception.WebRunException;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IServerService;
import com.nix.jingxun.addp.web.model.MemberModel;
import com.nix.jingxun.addp.web.model.ServerModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
    public Result create(@Valid @ModelAttribute ServerModel serverModel,
                         @RequestParam(value = "sshFile",required = false) MultipartFile proKey) {
        return Result.of(() -> {
            if (proKey != null) {
                byte[] bytes = new byte[10240];
                try {
                    int len = proKey.getInputStream().read(bytes);
                    serverModel.setSshKey(new String(bytes,0,len, StandardCharsets.UTF_8));
                } catch (IOException e) {
                    throw new WebRunException(Code.dataError,e.getMessage());
                }

            }
            MemberModel currentMember = MemberCache.currentUser();
            serverModel.setMemberId(currentMember == null ? -1 : currentMember.getId());
            serverModel.setPassword(AESUtil.encryption(serverModel.getPassword()));
            serverModel.setPassphrase(AESUtil.encryption(serverModel.getPassphrase()));
            if (serverModel.getId() != null) {
                return servicesService.update(serverModel);
            }
            return servicesService.save(serverModel);
        }).failFlat(this::failFlat).logFail();
    }

    @PostMapping("/list")
    @Clear
    public Result list(@ModelAttribute WebPageable webPageable, @RequestParam(value = "env",required = false) ADDPEnvironment environment) {
        return Result.of(() -> {
//            if (webPageable != null) {
//                Page<ServerModel> page = servicesService.memberServices(webPageable,environment);
//                page.getContent().forEach(s -> {
//                    s.setPassword(AESUtil.decrypt(s.getPassword()));
//                    s.setPassphrase(AESUtil.decrypt(s.getPassphrase()));
//                });
//                return page;
//            }
//            return Collections.emptyList();
            return ServerModel.builder().build();
        }).peek(serverModel -> serverModel.setCreateTime(LocalDateTime.now()))
                .failFlat(this::failFlat).logFail();
    }
    @ApiOperation("获取项目全部的服务器")
    @GetMapping("/psList/{pId}")
    public Result psList(@PathVariable Long pId) {
        return Result.of(() -> projectsService.findById(pId)._getServerModels()).failFlat(this::failFlat).logFail();
    }

}
