package com.nix.jingxun.addp.web.jpa;

import com.nix.jingxun.addp.web.model.ChangeBranchModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author keray
 * @date 2019/04/21 13:33
 */
public interface ChangeBranchJpa extends JpaRepository<ChangeBranchModel,Long> {
}
