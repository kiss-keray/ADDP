package com.nix.jingxun.addp.rpc.server;

import com.nix.jingxun.addp.rpc.producer.netty.NettyServer;
import javafx.application.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author keray
 * @date 2018/12/08 14:11
 */
@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试
@ContextConfiguration(locations={"classpath:application.xml"}) //加载配置文件
public class RPCTest {
    @Autowired
    private NettyServer nettyServer;

    @Test
    public void serializerTest() {
        nettyServer.start();
    }
}
