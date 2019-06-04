package com.nix.jingxun.addp.web.start.job;

import com.nix.jingxun.addp.web.IEnum.ReleaseType;
import com.nix.jingxun.addp.web.iservice.IReleaseBillService;
import com.nix.jingxun.addp.web.jpa.ReleaseBillJpa;
import com.nix.jingxun.addp.web.model.ReleaseBillModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author keray
 * @date 2019/06/02 19:36
 */
@Slf4j
@Configuration
public class ReleaseBillJob {
    private final static ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(10, r -> {
        Thread t = new Thread(r);
        t.setName("Scheduled");
        return t;
    });
    @Resource
    private IReleaseBillService releaseBillService;
    @Resource
    private ReleaseBillJpa releaseBillJpa;

    @Scheduled(cron = "0 0/10 * * * ? ")
    @Transactional(rollbackFor = Exception.class)
    public void autoProStart() {
        LocalDateTime now = LocalDateTime.now();
        List<ReleaseBillModel> billModels = releaseBillJpa.selectAllScStopBill(now.minusMinutes(10), now.minusMinutes(-10));
        log.info("检测到发布任务{}个",billModels.size());
        billModels.forEach(bill -> {
            int result = releaseBillJpa.updateBillType(bill.getId());
            if (result == 0) {
                return;
            }
            long timeout = bill.getReleaseTime().toEpochSecond(ZoneOffset.UTC) - now.toEpochSecond(ZoneOffset.UTC);
            SCHEDULED_EXECUTOR_SERVICE.schedule(() -> {
                releaseBillService.proStart(bill,true);
            },timeout, TimeUnit.SECONDS);
        });
    }
}
