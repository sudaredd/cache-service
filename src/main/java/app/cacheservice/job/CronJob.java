package app.cacheservice.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledThreadPoolExecutor;

@Component
@Slf4j
public class CronJob {


@Autowired
private ThreadPoolTaskScheduler threadPoolTaskScheduler;

//    @Scheduled(cron = "00 26 8,14,17,23 * * *")
    @Scheduled(fixedRate = 50000)
    public void runCron() {
        log.info("running job at "+ Thread.currentThread().getName());
        ScheduledThreadPoolExecutor scheduledExecutor = (ScheduledThreadPoolExecutor) threadPoolTaskScheduler.getScheduledExecutor();
        log.info(" activecount {} , task count {}", scheduledExecutor.getActiveCount(), scheduledExecutor.getTaskCount());

    }

    @RequiredArgsConstructor(onConstructor_ = @Autowired)
    @Component
    @Slf4j
    public static class FeeTimers {
        @Scheduled(cron = "00 09,08,10,11,12,13,14,15 5,11,20,23 * * *")
        public void feeBatchCommitTimer() {
            log.info("running job in staic at "+ Thread.currentThread().getName());
        }
    }
}
