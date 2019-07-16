package com.nix.jingxun.addp.web.model;

import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author keray
 * @date 2019/04/20 23:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Proxy(lazy = false)
@Table(name = "nix_member")
public class MemberModel extends BaseModel {
    @Column(length = 64)
    private String username;
    @Column(length = 32)
    private String password;

    public static void main(String[] args) {
        System.out.println(new MemberModel());
    }
}
