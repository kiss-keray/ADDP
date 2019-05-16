package com.nix.jingxun.addp.web.model;

import com.nix.jingxun.addp.web.base.SpringContextHolder;
import com.nix.jingxun.addp.web.iservice.IServicesService;
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
 * @date 2019/04/21 13:20
 */

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nix_projects")
@Proxy(lazy = false)
public class ProjectsModel implements Serializable {
    @Id

    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

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

    @NotNull
    private Long servicesId;

    @Transient
    private ServicesModel servicesModel;

    @Transient
    public ServicesModel getServicesModel() {
        if (servicesModel == null) {
            IServicesService servicesService = SpringContextHolder.getBean(IServicesService.class);
            servicesModel = servicesService.findById(servicesId);
        }
        return servicesModel;
    }
}
