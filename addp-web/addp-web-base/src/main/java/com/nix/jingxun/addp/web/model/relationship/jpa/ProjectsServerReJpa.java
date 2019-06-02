package com.nix.jingxun.addp.web.model.relationship.jpa;

import com.nix.jingxun.addp.web.model.relationship.model.ProjectsServerRe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author keray
 * @date 2019/05/20 15:56
 */
public interface ProjectsServerReJpa extends JpaRepository<ProjectsServerRe,Long> {

    @Modifying
    @Transactional
    @Query("delete from ProjectsServerRe where projectsId = :projectId")
    Integer deleteByProjectsId(@Param("projectId") Long projectId);

    @Query(value = "from ProjectsServerRe where serverId = :serverId")
    List<ProjectsServerRe> selectByServerId(@Param("serverId") Long serverId);
}
