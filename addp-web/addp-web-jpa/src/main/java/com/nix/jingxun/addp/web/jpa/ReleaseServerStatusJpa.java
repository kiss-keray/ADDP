package com.nix.jingxun.addp.web.jpa;

import com.nix.jingxun.addp.web.model.ReleaseServerStatusModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author keray
 * @date 2019/06/03 12:08
 */
public interface ReleaseServerStatusJpa extends JpaRepository<ReleaseServerStatusModel,Long> {
}
