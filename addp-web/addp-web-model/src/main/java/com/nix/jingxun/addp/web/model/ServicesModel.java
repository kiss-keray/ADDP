package com.nix.jingxun.addp.web.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author keray
 * @date 2019/04/21 13:09
 */

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nix_services")
public class ServicesModel implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Pattern(regexp = "[\\d]{1,3}:[\\d]{1,3}:[\\d]{1,3}:[\\d]{1,3}")
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
            JpaRepository jpaRepository = SpringContextHolder.getBean("memberJpa");
            memberModel = (MemberModel) jpaRepository.getOne(memberId);
        }
        return memberModel;
    }
}
