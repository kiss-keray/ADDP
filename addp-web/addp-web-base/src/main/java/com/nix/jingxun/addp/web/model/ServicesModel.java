package com.nix.jingxun.addp.web.model;
import com.nix.jingxun.addp.web.base.SpringContextHolder;
import com.nix.jingxun.addp.web.iservice.IMemberService;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author keray
 * @date 2019/04/21 13:09
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nix_services")
@Proxy(lazy = false)
public class ServicesModel  extends BaseModel {

    @Pattern(regexp = "[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}")
    @Column(nullable = false)
    private String ip;
    private Integer port;
    private String username;
    private String password;
    private String sshKey;
    private Long memberId;

    @Transient
    private MemberModel memberModel;

    public MemberModel getMember() {
        if (memberModel == null) {
            IMemberService memberService = SpringContextHolder.getBean(IMemberService.class);
            memberModel = memberService.findById(memberId);
        }
        return memberModel;
    }
}
