package com.nix.jingxun.addp.web.model;

import com.nix.jingxun.addp.web.base.SpringContextHolder;
import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.IEnum.ReleasePhase;
import com.nix.jingxun.addp.web.IEnum.ReleaseType;
import com.nix.jingxun.addp.web.iservice.IChangeBranchService;
import com.nix.jingxun.addp.web.iservice.IMemberService;
import com.nix.jingxun.addp.web.iservice.IReleaseServerStatusService;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author keray
 * @date 2019/05/20 18:05
 * 变更发布单
 * 出去正式环境，其他环境只会对应一个发布单
 * 正式环境每次发布产生一个发布单
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nix_release_bill")
@Proxy(lazy = false)
public class ReleaseBillModel extends BaseModel{
    // 发布时间 根据时间自动实现定时发布
    private LocalDateTime releaseTime;
    // 发布人
    @Column(nullable = false)
    private Long memberId;
    //发布变更
    @Column(nullable = false)
    private Long changeBranchId;
    // 发布状态
    @Enumerated(EnumType.STRING)
    private ReleaseType releaseType;
    // 当前发布阶段
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReleasePhase releasePhase;
    // 发布环境
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ADDPEnvironment environment;
    @Transient
    private ChangeBranchModel changeBranchModel;
    @Transient
    private MemberModel member;

    public ChangeBranchModel _getChangeBranchModel() {
        if (changeBranchModel == null) {
            IChangeBranchService changeBranchService = SpringContextHolder.getBean(IChangeBranchService.class);
            changeBranchModel = changeBranchService.findById(changeBranchId);
        }
        return changeBranchModel;
    }
    public MemberModel _getMember() {
        if (member == null) {
            IMemberService memberService = SpringContextHolder.getBean(IMemberService.class);
            member = memberService.findById(memberId);
        }
        return member;
    }
    @Transient
    private List<ReleaseServerStatusModel> releaseServerStatusModels;

    public List<ReleaseServerStatusModel> _getReleaseServerStatusModel() {
        if (releaseServerStatusModels == null) {
            IReleaseServerStatusService service = SpringContextHolder.getBean(IReleaseServerStatusService.class);
            releaseServerStatusModels = service.selectBillAllStatus(this);
        }
        return releaseServerStatusModels;
    }
}
