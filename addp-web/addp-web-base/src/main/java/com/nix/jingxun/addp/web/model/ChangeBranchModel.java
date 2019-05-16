package com.nix.jingxun.addp.web.model;

import com.nix.jingxun.addp.web.base.SpringContextHolder;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author keray
 * @date 2019/04/21 13:26
 */

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nix_change_branch")
@Proxy(lazy = false)
public class ChangeBranchModel implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String branchName;

    @Column(nullable = false)
    @NotNull
    private Long projectId;

    @Transient
    private ProjectsModel projectsModel;


    public ProjectsModel getProjectsModel() {
        if (projectsModel == null) {
            IProjectsService projectsService = SpringContextHolder.getBean(IProjectsService.class);
            projectsModel = projectsService.findById(projectId);
        }
        return projectsModel;
    }
}
