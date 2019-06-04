package com.nix.jingxun.addp.web.jpa;

import com.nix.jingxun.addp.web.IEnum.ADDPEnvironment;
import com.nix.jingxun.addp.web.model.ReleaseBillModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author keray
 * @date 2019/05/20 18:14
 */
public interface ReleaseBillJpa extends JpaRepository<ReleaseBillModel,Long> {

    @Query(value = "select b.* from nix_release_bill as b,nix_change_branch as c where b.change_branch_id = c.id and b.release_phase <> 'stop' and b.release_phase <> 'init' and b.environment = :env and c.project_id = :projectId",nativeQuery = true)
    ReleaseBillModel selectProjectBill(@Param("projectId") Long projectId,@Param("env") String env);

    @Query(value = "from ReleaseBillModel where changeBranchId = :changeId and environment = :env and releasePhase <> 'stop'")
    ReleaseBillModel selectChangeBill(@Param("changeId") Long changeId,@Param("env") ADDPEnvironment env);

    /**
     * 查询时间段内所有定时暂停的发布单
     * */
    @Query(value = "from ReleaseBillModel where releaseType = 'releaseSuccess' and releasePhase = 'build' and releaseTime between :startTime and :endTime")
    List<ReleaseBillModel> selectAllScStopBill(@Param("startTime") LocalDateTime start,@Param("endTime") LocalDateTime end);

    /**
     * 定时器将发布单的暂停状态改为等待状态
     *
     * */
    @Modifying(clearAutomatically = true)
    @Query(value = "update nix_release_bill set release_type = 'wait' , release_phase = 'start' where release_type = 'releaseSuccess' and release_phase = 'build' and id = :id",nativeQuery = true)
    Integer updateBillType(@Param("id")Long  id);

    @Query(value = "select b.* from nix_release_bill as b,nix_change_branch as c where b.change_branch_id = c.id and b.release_phase <> 'stop' and b.release_phase <> 'init'  and c.project_id = :projectId",nativeQuery = true)
    List<ReleaseBillModel> selectProjectsAllBill(@Param("projectId") Long projectId);
}
