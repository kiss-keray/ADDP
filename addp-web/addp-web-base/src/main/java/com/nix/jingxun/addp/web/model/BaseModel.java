package com.nix.jingxun.addp.web.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author keray
 * @date 2019/05/20 15:57
 */
@Data
@MappedSuperclass
public class BaseModel implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    protected Long id;
    @Column(columnDefinition="datetime DEFAULT CURRENT_TIMESTAMP")
    protected LocalDateTime createTime;

    @Column(nullable = false)
    protected LocalDateTime modifyTime;
}
