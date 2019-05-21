package com.nix.jingxun.addp.web.model;

import com.nix.jingxun.addp.web.base.SpringContextHolder;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

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
public class ChangeBranchModel  extends BaseModel {

    // 变更名
    @Column(nullable = false)
    private String name;

    // 变更对应分支
    @Column(nullable = false)
    private String branchName;


    @Column(nullable = false)
    @NotNull
    private Long projectId;


    @Transient
    private ProjectsModel projectsModel;


    public ProjectsModel _getProjectsModel() {
        if (projectsModel == null) {
            IProjectsService projectsService = SpringContextHolder.getBean(IProjectsService.class);
            projectsModel = projectsService.findById(projectId);
        }
        return projectsModel;
    }
}
