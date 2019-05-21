package com.nix.jingxun.addp.web.model;

import com.nix.jingxun.addp.web.base.SpringContextHolder;
import com.nix.jingxun.addp.web.model.relationship.jpa.ProjectsServiceReJpa;
import com.nix.jingxun.addp.web.model.relationship.model.ProjectsServiceRe;
import lombok.*;
import org.hibernate.annotations.Proxy;
import org.springframework.data.domain.Example;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author keray
 * @date 2019/04/21 13:20
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nix_projects")
@Proxy(lazy = false)
/**
 * 目前单机情况下 端口映射
 * docker内部定为80
 * 测试环境 44001端口
 * 预发环境 44002
 * 正式环境 44003
 * */
public class ProjectsModel extends BaseModel {
    // 项目中文名
    @Column(nullable = false)
    private String proName;
    // 项目名
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String gitUrl;

    private String gitUsername;

    private String gitPassword;

    private String gitToken;

    private String gitKey;

    @Column(columnDefinition="varchar(64) default 'master'")
    private String master;

    private Long memberId;

    // 项目测试环境域名
    private String testDomain;
    // 项目语法环境域名
    private String preDomain;
    // 项目正式环境域名
    private String proDomain;

    @Transient
    private List<ProjectsServiceRe> projectsServiceRes;

    @Transient
    public List<ServicesModel> getServicesModels () {
        if (projectsServiceRes == null) {
            ProjectsServiceReJpa jpa = SpringContextHolder.getBean(ProjectsServiceReJpa.class);
            ProjectsServiceRe example = ProjectsServiceRe.builder().projectsId(getId()).build();
            projectsServiceRes = jpa.findAll(Example.of(example));
        }
        return projectsServiceRes.stream().map(ProjectsServiceRe::getServicesModel).collect(Collectors.toList());
    }
}
