package com.nix.jingxun.addp.web.jpa;

import com.nix.jingxun.addp.web.diamond.ADDPEnvironment;
import com.nix.jingxun.addp.web.model.ServicesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author keray
 * @date 2019/04/21 13:51
 */
public interface ServicesJpa extends JpaRepository<ServicesModel,Long> {
    @Query(value = "from ServicesModel where id in :ids and environment = :environment")
    List<ServicesModel> selectEnvServices(@Param("ids") List<Long> ids, @Param("environment") ADDPEnvironment environment);
}
