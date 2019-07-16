package com.nix.jingxun.addp.web;

import com.nix.jingxun.addp.web.model.BaseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * @author keray
 * @date 2019/06/20 14:30
 */
@Component
@Slf4j
@Transactional
public class ModelListener {
    /**
     * 实体save前执行
     * */
    @PrePersist
    public void  prePersist(BaseModel model) {
        LocalDateTime now = LocalDateTime.now();
        model.setCreateTime(now);
        model.setModifyTime(now);
    }
    /**
     * 实体更新前执行
     * */
    @PreUpdate
    public void PreUpdate(BaseModel model) {
        model.setModifyTime(LocalDateTime.now());
    }


}
