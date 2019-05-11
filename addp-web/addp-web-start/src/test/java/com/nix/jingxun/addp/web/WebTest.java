package com.nix.jingxun.addp.web;

import com.nix.jingxun.addp.web.iservice.IMemberService;
import com.nix.jingxun.addp.web.iservice.IServicesService;
import com.nix.jingxun.addp.web.model.MemberModel;
import com.nix.jingxun.addp.web.model.ServicesModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author keray
 * @date 2019/05/11 11:04
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WebTest {

    @Resource
    private IMemberService memberService;
    @Resource
    private IServicesService servicesService;

    @Test
    public void jpaBeanTest() {

    }

    @Test
    public void modelJpaTest() {
        ServicesModel model = ServicesModel.builder().memberId(1L).build();
        System.out.println(model.getMember());
    }

    @Test
    public void createMember() {
        MemberModel memberModel = MemberModel.builder()
                .username("root")
                .password("Kiss4400").build();
        try {
            memberService.save(memberModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createService() {
        ServicesModel model = ServicesModel.builder()
                .memberId(1L)
                .ip("59.110.234.213")
                .username("root")
                .password("Kiss4400")
                .port(22).build();
        try {
            servicesService.save(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
