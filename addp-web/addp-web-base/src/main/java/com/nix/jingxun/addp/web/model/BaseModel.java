package com.nix.jingxun.addp.web.model;

import com.nix.jingxun.addp.web.ModelListener;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author keray
 * @date 2019/05/20 15:57
 */
@Data
@MappedSuperclass
@EntityListeners(ModelListener.class)
public class BaseModel implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    protected Long id;
    @CreatedDate
    protected LocalDateTime createTime;

    @LastModifiedDate
    protected LocalDateTime modifyTime;

    @Column(columnDefinition = "bit default 0")
    protected boolean delFlag;
}
