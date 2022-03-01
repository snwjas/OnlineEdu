package xyz.refrain.onlineedu.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.refrain.onlineedu.service.StatService;

/**
 * 数据统计定时任务
 *
 * @author Myles Yang
 */
@Component
@Slf4j
public class StatTask {

	@Autowired
	private StatService statService;

	/**
	 * 每天零点统计昨日的信息
	 */
	@Async
	@Scheduled(cron = "1 0 0 */1 * ?")
	public void autoStat() {

		statService.statDaily();

	}

}
