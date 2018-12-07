package com.nix.jingxun.addp.rpc.server;

import com.nix.jingxun.addp.rpc.consumer.annotation.RPCConsumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;

/**
 * @author keray
 * @date 2018/12/07 16:03
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class RPCConsumerAnnoTest {

    @Test
    public void ConsumerAnnoTest() {
        ConsumerTest test = new ConsumerTest();
    }
}
