package com.nix.jingxun.addp.web;

import com.jcraft.jsch.JSchException;
import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.iservice.IMemberService;
import com.nix.jingxun.addp.web.iservice.IReleaseBillService;
import com.nix.jingxun.addp.web.iservice.IServerService;
import com.nix.jingxun.addp.web.jpa.MemberJpa;
import com.nix.jingxun.addp.web.jpa.ServerJpa;
import com.nix.jingxun.addp.web.model.MemberModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author keray
 * @date 2019/05/21 18:46
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebStart.class)
public class SpringTest {
    @Resource
    private ServerJpa serverJpa;
    @Resource
    private IServerService servicesService;
    @Resource
    private IReleaseBillService releaseBillService;
    @Resource
    private IMemberService memberService;
    @Resource
    private MemberJpa memberJpa;

    @Test
    public void jpaInTest() {
        ProjectsModel projectsModel = new ProjectsModel();
        projectsModel.setId(1L);
        System.out.println(servicesService.selectEnvAllowServer(projectsModel,ADDPEnvironment.test));
    }

    @Test
    public void findById() {
        System.out.println(serverJpa.getOne(100L));
    }

    @Test
    public void selectProjectBill() {
        System.out.println(releaseBillService.selectProjectBill(4L,ADDPEnvironment.test));
    }

    @Test
    public void sshKey() throws IOException, JSchException {
        servicesService.shellExeByUsername(servicesService.findById(4L));
    }

    @Test
    public void addTest() {
        memberService.add("testt","xxx");
    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void transTest() {
        memberJpa.save(MemberModel.builder().username("testtt").password("").build());
        throw new RuntimeException("xxxx");
    }
}
