package com.nix.jingxun.addp.web.start.controller;

import cn.hutool.core.map.MapUtil;
import com.nix.jingxun.addp.common.Result;
import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.IEnum.ReleasePhase;
import com.nix.jingxun.addp.web.IEnum.ReleaseType;
import com.nix.jingxun.addp.web.iservice.IChangeBranchService;
import com.nix.jingxun.addp.web.iservice.IReleaseBillService;
import com.nix.jingxun.addp.web.model.ChangeBranchModel;
import com.nix.jingxun.addp.web.model.ReleaseBillModel;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

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
            if (model != null) {
                model.setMember(model._getMember());
                model.setChangeBranchModel(model._getChangeBranchModel());
            }
            return model;
        }).failFlat(this::failFlat).logFail();
    }

    @GetMapping("/projectBill/{env}")
    public Result getProjectBill(@RequestParam("projectId") Long projectId, @PathVariable ADDPEnvironment env) {
        return Result.of(() -> releaseBillService.selectProjectBill(projectId,env))
                .peek(bill -> {
                    if (bill != null) {
                        bill.setChangeBranchModel(bill._getChangeBranchModel());
                    }
                })
                .failFlat(this::failFlat).logFail();
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
                return MapUtil.<String, Object>builder("bill", billModel).put("status", releaseBillService.pullCode(billModel)).build();
            } catch (Exception e) {
                return Result.fail(e);
            }
        }).failFlat(this::failFlat).logFail();
    }

    @GetMapping("/build")
    public Result build(@RequestParam("id") Long id) {
        return Result.of(() -> {
            try {
                ReleaseBillModel model = releaseBillService.findById(id);
                return MapUtil.builder("bill",releaseBillService.build(model)).put("status",releaseBillService.build(model));
            } catch (Exception e) {
                return Result.fail(e);
            }
        }).failFlat(this::failFlat).logFail();
    }

    @GetMapping("/startApp")
    public Result startApp(@RequestParam("id") Long id) {
        return Result.of(() -> {
            try {
                ReleaseBillModel model = releaseBillService.findById(id);
                return MapUtil.builder("bill",releaseBillService.build(model)).put("status",releaseBillService.startApp(model));
            } catch (Exception e) {
                return Result.fail(e);
            }
        }).failFlat(this::failFlat).logFail();
    }
    @GetMapping("/down")
    public Result billDown(@RequestParam("id") Long id) {
        return Result.of(() -> {
            ReleaseBillModel bill = null;
            try {
                bill = releaseBillService.billDown(id);
            } catch (Exception e) {
                return e;
            }
            Map<String,Object> result = MapUtil.<String,Object>builder("bill",bill)
                    .put("status",false)
                    .build();
            if (bill.getReleasePhase() == ReleasePhase.init) {
                result.put("status",true);
            }
            return result;
        }).failFlat(this::failFlat).logFail();
    }

    @GetMapping("/status")
    public Result gitBillStatus(@RequestParam("id") Long id) {
        return Result.of(() -> releaseBillService.findById(id)).failFlat(this::failFlat).logFail();
    }

    @GetMapping("/autoRelease")
    public Result autoRelease(@RequestParam("id") Long id) {
        return Result.of(() -> {
            try {
                ReleaseBillModel bill = releaseBillService.deployBranch(releaseBillService.findById(id));
                bill.setChangeBranchModel(bill._getChangeBranchModel());
                return bill;
            } catch (Exception e) {
                return e;
            }
        }).failFlat(this::failFlat).logFail();
    }

}
