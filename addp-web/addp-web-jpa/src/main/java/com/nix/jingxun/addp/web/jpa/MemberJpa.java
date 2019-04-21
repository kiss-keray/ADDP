package com.nix.jingxun.addp.web.jpa;

import com.nix.jingxun.addp.web.model.MemberModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author keray
 * @date 2019/04/21 0:38
 */
public interface MemberJpa extends JpaRepository<MemberModel,Integer> {
}
