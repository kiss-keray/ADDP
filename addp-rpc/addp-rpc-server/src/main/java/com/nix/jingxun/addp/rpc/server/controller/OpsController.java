package com.nix.jingxun.addp.rpc.server.controller;

import com.nix.jingxun.addp.rpc.common.Producer2ServerRequest;
import com.nix.jingxun.addp.rpc.common.RPCMethodParser;
import com.nix.jingxun.addp.rpc.server.Result;
import com.nix.jingxun.addp.rpc.server.service.OpsService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author keray
 * @date 2018/12/25 15:14
 */
@RestController
@RequestMapping("/ops")
public class OpsController {
    @Resource
    private OpsService opsService;

    /**
     * 服务查询
     * @param key
     * @param type 查询方式
     * */
    @GetMapping("/search/{type}")
    public Result<List<RPCMethodParser.ServiceModel>> search(@RequestParam("key") String key, @PathVariable OpsService.SearchType type) {
        return Result.of(() -> {
            if (StringUtils.isEmpty(key)) {
                return Collections.emptyList();
            }
            return opsService.search(key, type);
        });
    }

    /**
     * ops 控制台测试时获取接口详情
     * */
    @GetMapping("/detail")
    public Result<Producer2ServerRequest> interfaceDetail(@RequestParam("sign") String sign) {
        return Result.of(() -> opsService.serviceDetail(sign));
    }

    /**
     * 获取服务接口的所有提供方
     * @return ['192.168.0.1:15000']
     * */
    @GetMapping("/producers")
    public Result<List<String>> producers(@RequestParam("sign") String sign) {
        return Result.of(() -> opsService.producers(sign));
    }
}
