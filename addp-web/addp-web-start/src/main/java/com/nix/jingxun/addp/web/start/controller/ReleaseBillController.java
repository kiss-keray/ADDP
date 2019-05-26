package com.nix.jingxun.addp.web.start.controller;

import cn.hutool.core.map.MapUtil;
import com.nix.jingxun.addp.common.Result;
import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.iservice.IChangeBranchService;
import com.nix.jingxun.addp.web.iservice.IReleaseBillService;
import com.nix.jingxun.addp.web.model.ChangeBranchModel;
import com.nix.jingxun.addp.web.model.ReleaseBillModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author keray
 * @date 2019/05/26 18:03
 */

@RestController
@RequestMapping("/release")
public class ReleaseBillController extends BaseController {
    @Resource
    private IReleaseBillService releaseBillService;
    @Resource
    private IChangeBranchService changeBranchService;

    @GetMapping("/me")
    public Result me(@RequestParam("id") Long id) {
        return Result.of(() -> {
            ReleaseBillModel model = releaseBillService.findById(id);
            if (model == null) {
                return null;
            }
            model.setChangeBranchModel(model._getChangeBranchModel());
            return model;
        }).failFlat(this::failFlat).logFail();
    }

    @GetMapping("/selectChangeRB")
    public Result selectChangeRB(@RequestParam("changeId") Long changeId, @RequestParam("env") ADDPEnvironment environment) {
        return Result.of(() -> {
            ReleaseBillModel model = releaseBillService.changeBill(changeId, environment);
            model.setMember(model._getMember());
            model.setChangeBranchModel(model._getChangeBranchModel());
            return model;
        }).failFlat(this::failFlat).logFail();
    }

    @GetMapping("/pullCode")
    public Result pullCode(@RequestParam("changeId") Long changeId, @RequestParam("env") ADDPEnvironment environment) {
        return Result.of(() -> {
            try {
                ChangeBranchModel model = changeBranchService.findById(changeId);
                ReleaseBillModel billModel = releaseBillService.changeBill(changeId, environment);
                if (billModel == null) {
                    billModel = releaseBillService.createBill(model, environment);
                }
                return MapUtil.<String, Object>builder("model", billModel).put("status", releaseBillService.pullCode(billModel)).build();
            } catch (Exception e) {
                return Result.fail(e);
            }
        }).failFlat(this::failFlat).logFail();
    }

    @GetMapping("/build")
    public Result build(@RequestParam("id") Long id) {
        return Result.of(() -> {
            try {
                return releaseBillService.build(releaseBillService.findById(id));
            } catch (Exception e) {
                return Result.fail(e);
            }
        }).failFlat(this::failFlat).logFail();
    }

    @GetMapping("/startApp")
    public Result startApp(@RequestParam("id") Long id) {
        return Result.of(() -> {
            try {
                return releaseBillService.startApp(releaseBillService.findById(id));
            } catch (Exception e) {
                return Result.fail(e);
            }
        }).failFlat(this::failFlat).logFail();
    }
}
