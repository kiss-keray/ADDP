package com.nix.jingxun.addp.web;

import com.nix.jingxun.addp.web.common.config.WebConfig;
import com.nix.jingxun.addp.web.iservice.IChangeBranchService;
import com.nix.jingxun.addp.web.iservice.IMemberService;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IServicesService;
import com.nix.jingxun.addp.web.model.ChangeBranchModel;
import com.nix.jingxun.addp.web.model.MemberModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServicesModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author keray
 * @date 2019/05/11 11:04
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan("com.nix.jingxun.addp.*")
public class WebTest {

    @Resource
    private IMemberService memberService;
    @Resource
    private IServicesService servicesService;
    @Resource
    private IProjectsService projectsService;
    @Resource
    private IChangeBranchService changeBranchService;
    @Resource
    private WebConfig webConfig;

    @Test
    public void jpaBeanTest() {

    }

    @Test
    public void modelJpaTest() {
        ServicesModel model = servicesService.findById(1L);
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
                .id(1L)
                .memberId(1L)
                .ip("59.110.234.212")
                .username("root")
                .password("Kiss4400")
                .port(22).build();
        try {
            servicesService.update(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createProject() {
        ProjectsModel projectsModel = ProjectsModel.builder()
                .servicesId(1L)
                .memberId(1L)
                .name("notes")
                .gitUrl("https://github.com/kiss-yu/notes.git")
                .gitUsername("1172304645@qq.com")
                .gitPassword("a1172304645")
                .build();
        try {
            projectsService.save(projectsModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createChangeBranch() {
        ChangeBranchModel model = ChangeBranchModel.builder()
                .name("测试")
                .branchName("test1")
                .projectId(8L)
                .build();
        try {
            changeBranchService.save(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
