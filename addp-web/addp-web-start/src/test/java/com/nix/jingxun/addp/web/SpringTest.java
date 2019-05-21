package com.nix.jingxun.addp.web;

import com.nix.jingxun.addp.web.diamond.ADDPEnvironment;
import com.nix.jingxun.addp.web.iservice.IServicesService;
import com.nix.jingxun.addp.web.jpa.ServicesJpa;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author keray
 * @date 2019/05/21 18:46
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan("com.nix.jingxun.addp.*")
public class SpringTest {
    @Resource
    private ServicesJpa servicesJpa;
    @Resource
    private IServicesService servicesService;

    @Test
    public void jpaInTest() {
        ProjectsModel projectsModel = new ProjectsModel();
        projectsModel.setId(1L);
        System.out.println(servicesService.selectEnvServices(projectsModel,ADDPEnvironment.test));
    }
}
