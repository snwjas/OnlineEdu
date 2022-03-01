package xyz.refrain.onlineedu.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.refrain.onlineedu.config.LocalDateTimeSerializerConfig;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.service.StatService;

import java.time.LocalDateTime;

/**
 * 数据统计控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminStatController")
@RequestMapping("/api/admin/stat")
@Api(value = "后台数据统计控制器", tags = {"后台数据统计接口"})
public class StatController {

	@Autowired
	private StatService statService;

	@GetMapping("/get/common")
	@ApiOperation("获取平台数据统计")
	public R getCommon() {
		return statService.getCommonStat();
	}

	@PostMapping("/get/daily")
	@ApiOperation("获取平台每日数据统计")
	public R getDaily(@RequestParam("start") @DateTimeFormat(pattern =
			LocalDateTimeSerializerConfig.DEFAULT_DATE_TIME_PATTERN) LocalDateTime start,
	                  @RequestParam("end") @DateTimeFormat(pattern =
			LocalDateTimeSerializerConfig.DEFAULT_DATE_TIME_PATTERN) LocalDateTime end) {
		return statService.getDailyStat(start, end);
	}
}
