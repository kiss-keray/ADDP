package com.nix.jingxun.addp.web.jpa;

import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.model.ServerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author keray
 * @date 2019/04/21 13:51
 */
public interface ServerJpa extends JpaRepository<ServerModel,Long> {
    @Query(value = "from ServerModel where id in :ids and environment = :environment")
    List<ServerModel> selectEnvServices(@Param("ids") List<Long> ids, @Param("environment") ADDPEnvironment environment);

    @Modifying(clearAutomatically = true)
    @Query(value = "update ServerModel set allowRestart = true where id in :ids")
    Integer updateProAllow(@Param("ids") List<Long> ids);
}
