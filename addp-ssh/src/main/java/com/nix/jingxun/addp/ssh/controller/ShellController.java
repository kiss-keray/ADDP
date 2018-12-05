package com.nix.jingxun.addp.ssh.controller;

import com.alibaba.fastjson.JSON;
import com.nix.jingxun.addp.ssh.util.FluentMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author keray
 * @date 2018/12/04 下午3:06
 */
@RestController
@RequestMapping("/ssh/shell")
public class ShellController {

    @Autowired
    private StringRedisTemplate template;

    @PostMapping("/create")
    public String createShell(@RequestParam("ip") String ip, @RequestParam("username") String username, @RequestParam("password") String password) {
        String id = System.currentTimeMillis() + ip;
        Map<String,String> sshMsg = FluentMaps.newMap("ip",ip).
                plus("username",username)
                .plus("password",password);
        template.opsForValue().set(id, JSON.toJSONString(sshMsg),2, TimeUnit.MINUTES);
        return id;
    }
    @GetMapping("/get/{id}")
    public String getSSHId(@PathVariable(name = "id") String id) {
        return template.opsForValue().get(id);
    }

}
