package com.nix.jingxun.addp.rpc.server;

import com.nix.jingxun.addp.rpc.consumer.proxy.RPCConsumerFactory;
import com.nix.jingxun.addp.rpc.producer.test.Hello;
import com.nix.jingxun.addp.rpc.producer.test.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author keray
 * @date 2018/12/07 16:48
 */

// @RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试
//@ContextConfiguration(locations={"classpath:application.xml"}) //加载配置文件
public class ConsumerTest {
    @Test
    public void main() throws TimeoutException, InterruptedException {
        Hello hello = RPCConsumerFactory.consumer(Hello.class,10000,null);
        System.out.println(hello.getHello());
        hello.sayHello("sayHello");
        hello.sayHello1(Arrays.asList("a","b","c"));
        User user = hello.updateUser(new User("99",20,new User("100",19,null)),true);
        System.out.println(user.getChild());
    }
}