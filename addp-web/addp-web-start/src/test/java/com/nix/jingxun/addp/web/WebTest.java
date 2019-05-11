package com.nix.jingxun.addp.web;
import com.nix.jingxun.addp.web.model.MemberModel;

import com.nix.jingxun.addp.web.iservice.IMemberService;
import com.nix.jingxun.addp.web.jpa.MemberJpa;
import com.nix.jingxun.addp.web.model.ServicesModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
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
    private ApplicationContext context;

    @Test
    public void jpaBeanTest() {
        System.out.println(context.getBean("memberJpa"));
    }

    @Test
    public void modelJpaTest() {
        System.out.println(context.getBean("memberJpa"));
        JpaRepository jpaRepository = (JpaRepository) context.getBean("memberJpa");
        ServicesModel model = new ServicesModel();
        model.setMemberId(1L);
        System.out.println(jpaRepository.getOne(model.getMemberId()));
        System.out.println(model.getMember());
    }
}
