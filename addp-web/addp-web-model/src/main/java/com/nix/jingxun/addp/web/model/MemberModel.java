package com.nix.jingxun.addp.web.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author keray
 * @date 2019/04/20 23:30
 */
//@Data
//@Builder
@Entity
@Table(indexes = {@Index(name = "index_username",columnList = "username",unique = true)})
public class MemberModel implements Serializable {
    @Id
    private Integer id;
    private String username;
    @Column(length = 32)
    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
