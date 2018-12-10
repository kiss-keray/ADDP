package com.nix.jingxun.addp.rpc.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * @author keray
 * @date 2018/12/08 14:28
 */@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:application.xml"})
public class Main {

    @Test
    public void main() throws IOException {
        System.in.read(new byte[1]);
    }
}
