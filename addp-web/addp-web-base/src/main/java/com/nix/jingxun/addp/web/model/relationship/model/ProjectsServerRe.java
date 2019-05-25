package com.nix.jingxun.addp.web.model.relationship.model;

import com.nix.jingxun.addp.web.base.SpringContextHolder;
import com.nix.jingxun.addp.web.iservice.IProjectsService;
import com.nix.jingxun.addp.web.iservice.IServerService;
import com.nix.jingxun.addp.web.model.ProjectsModel;
import com.nix.jingxun.addp.web.model.ServerModel;
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
@Table(name = "nix_projects_server_re")
@Proxy(lazy = false)
public class ProjectsServerRe implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    protected Long id;

    @NotNull
    private Long projectsId;
    @NotNull
    private Long serverId;


    @Transient
    private ServerModel serverModel;
    @Transient
    private ProjectsModel projectsModel;

    public ServerModel _getServerModel() {
        if (serverModel == null) {
            IServerService servicesService = SpringContextHolder.getBean(IServerService.class);
            serverModel = servicesService.findById(serverId);
        }
        return serverModel;
    }

    public ProjectsModel _getProjectsModel() {
        if (projectsModel == null) {
            IProjectsService projectsService = SpringContextHolder.getBean(IProjectsService.class);
            projectsModel = projectsService.findById(projectsId);
        }
        return projectsModel;
    }
}
