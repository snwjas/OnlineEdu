package xyz.refrain.onlineedu.model.vo.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import xyz.refrain.onlineedu.config.LocalDateTimeSerializerConfig;
import xyz.refrain.onlineedu.model.base.BeanConvert;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 每日数据统计信息
 *
 * @author Myles Yang
 */
@Accessors(chain = true)
@Data
public class StatDailyVO implements Serializable, BeanConvert {

	private static final long serialVersionUID = 2634477720113002360L;

	@JsonFormat(pattern = LocalDateTimeSerializerConfig.DEFAULT_DATE_PATTERN)
	@ApiModelProperty(value = "统计日期")
	private LocalDateTime date;

	@ApiModelProperty(value = "访问人数")
	private Integer visitCount;

	@ApiModelProperty(value = "注册人数")
	private Integer registerCount;

	@ApiModelProperty(value = "活跃人数")
	private Integer loginCount;

	@ApiModelProperty(value = "每日播放视频数")
	private Integer videoViewCount;

	@ApiModelProperty(value = "每日新增课程数")
	private Integer courseBuyCount;

}
