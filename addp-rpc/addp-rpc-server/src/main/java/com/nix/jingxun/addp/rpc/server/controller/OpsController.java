package com.nix.jingxun.addp.rpc.server.controller;

import com.nix.jingxun.addp.rpc.common.RPCMethodParser;
import com.nix.jingxun.addp.rpc.server.service.OpsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
    @PostMapping("/search")
    public List<RPCMethodParser.ServiceModel> search(@RequestParam("key") String key,@RequestParam(name = "type",defaultValue = "service") OpsService.SearchType type) {
        return opsService.search(key, type);
    }
}
