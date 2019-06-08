package com.nix.jingxun.addp.web.model;

import com.nix.jingxun.addp.web.IEnum.ReleasePhase;
import com.nix.jingxun.addp.web.IEnum.ReleaseType;
import com.nix.jingxun.addp.web.base.SpringContextHolder;
import com.nix.jingxun.addp.web.iservice.IReleaseBillService;
import com.nix.jingxun.addp.web.iservice.IServerService;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author keray
 * @date 2019/06/03 11:55
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nix_release_server_status")
@Proxy(lazy = false)
public class ReleaseServerStatusModel extends BaseModel{
    private Long billId;
    private Long serverId;
    @Enumerated(EnumType.STRING)
    private ReleasePhase releasePhase;

    @Enumerated(EnumType.STRING)
    private ReleaseType releaseType;

    private LocalDateTime oneStartTime;
    private LocalDateTime oneFinishTime;


    private LocalDateTime twoStartTime;
    private LocalDateTime twoFinishTime;


    private LocalDateTime threeStartTime;
    private LocalDateTime threeFinishTime;


    @Column(columnDefinition="text")
    private String releaseData;
    @Transient
    public int getOneTime() {
        if (oneFinishTime != null && oneStartTime != null) {
            return (int) (oneFinishTime.toEpochSecond(ZoneOffset.UTC) - oneStartTime.toEpochSecond(ZoneOffset.UTC));
        }
        return 0;
    }

    @Transient
    public int getTwoTime() {
        if (twoFinishTime != null && twoStartTime != null) {
            return (int) (twoFinishTime.toEpochSecond(ZoneOffset.UTC) - twoStartTime.toEpochSecond(ZoneOffset.UTC));
        }
        return 0;
    }

    @Transient
    public int getThreeTime() {
        if (threeFinishTime != null && threeStartTime != null) {
            return (int) (threeFinishTime.toEpochSecond(ZoneOffset.UTC) - threeStartTime.toEpochSecond(ZoneOffset.UTC));
        }
        return 0;
    }

    @Transient
    private ReleaseBillModel releaseBillModel;
    @Transient
    private ServerModel serverModel;

    public ReleaseBillModel _getReleaseBillModel() {
        if (releaseBillModel == null) {
            IReleaseBillService releaseBillService = SpringContextHolder.getBean(IReleaseBillService.class);
            releaseBillModel = releaseBillService.findById(billId);
        }
        return releaseBillModel;
    }

    public ServerModel _getServerModel() {
        if (serverModel == null) {
            IServerService serverService = SpringContextHolder.getBean(IServerService.class);
            serverModel = serverService.findById(serverId);
        }
        return serverModel;
    }

}
