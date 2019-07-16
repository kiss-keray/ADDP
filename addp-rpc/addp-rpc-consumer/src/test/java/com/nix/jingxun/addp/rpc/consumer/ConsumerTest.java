package com.nix.jingxun.addp.rpc.consumer;
import com.nix.jingxun.addp.web.iservice.IMemberService;
import com.nix.jingxun.addp.web.model.MemberModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeoutException;

/**
 * @author keray
 * @date 2018/12/07 16:48
 */

 @RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试
@ContextConfiguration(locations={"classpath:application.xml"}) //加载配置文件
public class ConsumerTest {
     @Autowired
     private ApplicationContext applicationContext;

    @Test
    public void main() throws TimeoutException, InterruptedException {
//        hello.sayHello1(Collections.emptyList());
//        User user = hello.updateUser(new User("99",20,new User("100",19,null)),null);
//        System.out.println(user.getChild());
    }

    @Test
    public void transactionalTest() {
        IMemberService memberService = applicationContext.getBean(IMemberService.class);
        MemberModel memberModel = memberService.add("test7","test");
        System.out.println("member==" + memberModel.toString());
    }
}
