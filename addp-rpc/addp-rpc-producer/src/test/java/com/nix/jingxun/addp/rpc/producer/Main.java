package com.nix.jingxun.addp.rpc.producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author keray
 * @date 2018/12/08 14:28
 */@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:application.xml"})
public class Main {

     @Autowired
     private ApplicationContext applicationContext;
    @Test
    public void main() throws Exception {
        System.in.read(new byte[1]);
    }
}
