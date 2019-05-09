package com.nix.jingxun.addp.web.start.controller;

import com.nix.jingxun.addp.web.iservice.IServicesService;
import com.nix.jingxun.addp.web.model.MemberModel;
import com.nix.jingxun.addp.web.model.ServicesModel;
import com.nix.jingxun.addp.web.common.Result;
import com.nix.jingxun.addp.web.common.cache.MemberCache;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author keray
 * @date 2019/04/21 17:26
 */
@RestController
@RequestMapping("/service")
public class ServicesController {

    @Resource
    private IServicesService servicesService;

    @PostMapping("/create")
    public Result create(@Valid @ModelAttribute ServicesModel servicesModel) {
        return Result.of(() -> {
            MemberModel currentMember = MemberCache.currentUser();
            servicesModel.setMemberId(currentMember == null ? -1 : currentMember.getId());
            try {
                return servicesService.save(servicesModel);
            } catch (Exception e) {
                e.printStackTrace();
                return Result.fail(e);
            }

        }).logFail();
    }

}
