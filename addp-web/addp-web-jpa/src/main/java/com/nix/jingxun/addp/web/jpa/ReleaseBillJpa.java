package com.nix.jingxun.addp.web.jpa;

import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.model.ReleaseBillModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author keray
 * @date 2019/05/20 18:14
 */
public interface ReleaseBillJpa extends JpaRepository<ReleaseBillModel,Long> {

}
