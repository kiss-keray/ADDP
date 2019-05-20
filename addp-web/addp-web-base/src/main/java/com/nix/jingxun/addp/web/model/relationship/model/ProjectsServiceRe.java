package com.nix.jingxun.addp.web.model.relationship.model;

import com.nix.jingxun.addp.web.base.SpringContextHolder;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IServicesService;
import com.nix.jingxun.addp.web.model.BaseModel;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServicesModel;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author keray
 * @date 2019/05/20 15:46
 */
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nix_projects_services")
@Proxy(lazy = false)
public class ProjectsServiceRe  implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    protected Long id;

    @NotNull
    private Long projectsId;
    @NotNull
    private Long servicesId;


    @Transient
    private ServicesModel servicesModel;
    @Transient
    private ProjectsModel projectsModel;

    public ServicesModel getServicesModel() {
        if (servicesModel == null) {
            IServicesService servicesService = SpringContextHolder.getBean(IServicesService.class);
            servicesModel = servicesService.findById(servicesId);
        }
        return servicesModel;
    }

    public ProjectsModel getProjectsModel() {
        if (projectsModel == null) {
            IProjectsService projectsService = SpringContextHolder.getBean(IProjectsService.class);
            projectsModel = projectsService.findById(projectsId);
        }
        return projectsModel;
    }
}
