package com.nix.jingxun.addp.web.model;

import com.nix.jingxun.addp.web.base.SpringContextHolder;
import com.nix.jingxun.addp.web.model.relationship.jpa.ProjectsServiceReJpa;
import com.nix.jingxun.addp.web.model.relationship.model.ProjectsServiceRe;
import lombok.*;
import org.hibernate.annotations.Proxy;
import org.springframework.data.domain.Example;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
public class ProjectsModel extends BaseModel {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String gitUrl;

    private String gitUsername;

    private String gitPassword;

    private String gitToken;

    private String gitKey;

    private String master;

    private Long memberId;

    @Transient
    private List<ProjectsServiceRe> projectsServiceRes;


    @Transient
    public ServicesModel getServicesModel() {
        return getServicesModels().get(0);
    }

    public List<ServicesModel> getServicesModels () {
        if (projectsServiceRes == null) {
            ProjectsServiceReJpa jpa = SpringContextHolder.getBean(ProjectsServiceReJpa.class);
            ProjectsServiceRe example = ProjectsServiceRe.builder().projectsId(getId()).build();
            projectsServiceRes = jpa.findAll(Example.of(example));
        }
        return projectsServiceRes.stream().map(ProjectsServiceRe::getServicesModel).collect(Collectors.toList());
    }
}
