package com.nix.jingxun.addp.rpc.server;

import com.nix.jingxun.addp.rpc.producer.test.Hello;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author keray
 * @date 2018/12/07 16:48
 */

 @RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试
@ContextConfiguration(locations={"classpath:application.xml"}) //加载配置文件
public class ConsumerTest {

    @Autowired
    private ApplicationContext application;
    @Test
    public void main() {
        Hello hello = application.getBean(Hello.class);
        System.out.println(hello.getHello());
        hello.sayHello("hahahhaha");
    }
}