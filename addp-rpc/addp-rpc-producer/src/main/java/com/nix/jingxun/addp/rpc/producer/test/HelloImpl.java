package com.nix.jingxun.addp.rpc.producer.test;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author keray
 * @date 2018/12/07 22:42
 */
@Component
public class HelloImpl implements Hello {

    @Override
    public void sayHello(String str) {
        System.out.println(str);
    }

    @Override
    public void sayHello1(List<String> strs) {
        System.out.println("xsxsaxsaxsaxsa");
        System.out.println(Arrays.toString(strs.toArray()));
    }

    @Override
    public String getHello() {
        return "getHello";
    }

    @Override
    public User updateUser(User user, Boolean clear) throws TimeoutException, InterruptedException {
        return new User("xsxs",22,null);
    }
}
