package com.nix.jingxun.addp.web.jpa;

import com.nix.jingxun.addp.web.model.ServicesModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author keray
 * @date 2019/04/21 13:51
 */
public interface ServicesJpa extends JpaRepository<ServicesModel,Long> {
}
