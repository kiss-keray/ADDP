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

    @Query(value = "select b.* from nix_release_bill as b,nix_change_branch as c where b.change_branch_id = c.id and b.release_phase <> 'stop' and b.release_phase <> 'init' and b.environment = :env and c.project_id = :projectId",nativeQuery = true)
    ReleaseBillModel selectProjectBill(@Param("projectId") Long projectId,@Param("env") String env);

    @Query(value = "from ReleaseBillModel where changeBranchId = :changeId and environment = :env and releasePhase <> 'stop'")
    ReleaseBillModel selectChangeBill(@Param("changeId") Long changeId,@Param("env") ADDPEnvironment env);
}
