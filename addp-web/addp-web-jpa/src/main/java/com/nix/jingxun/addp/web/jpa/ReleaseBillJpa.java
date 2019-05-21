package com.nix.jingxun.addp.web.jpa;

import com.nix.jingxun.addp.web.model.ReleaseBillModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author keray
 * @date 2019/05/20 18:14
 */
public interface ReleaseBillJpa extends JpaRepository<ReleaseBillModel,Long> {
}
