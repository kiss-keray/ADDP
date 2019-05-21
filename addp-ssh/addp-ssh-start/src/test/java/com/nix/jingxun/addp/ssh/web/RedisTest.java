package com.nix.jingxun.addp.ssh.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author keray
 * @date 2018/12/05 下午6:21
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisTemplate template;

    @Test
    public void redisContentTest() {
        if(!template.hasKey("jingxun")){
            template.opsForValue().append("jingxun", "静寻");
            template.opsForValue().append("jingxun1", "静寻1");
            System.out.println("使用redis缓存保存数据成功");
        }else{
            template.delete("jingxun");
            System.out.println("key已存在");
        }
    }
}
