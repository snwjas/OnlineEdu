package xyz.refrain.onlineedu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 网站统计日数据
 * </p>
 *
 * @author Myles Yang
 * @since 2021-01-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("stat_daily")
@ApiModel(value = "StatDailyEntity对象", description = "网站统计日数据")
public class StatDailyEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主键")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "统计日期")
	@TableField("date")
	private LocalDateTime date;

	@ApiModelProperty(value = "访问人数")
	@TableField("visit_count")
	private Integer visitCount;

	@ApiModelProperty(value = "注册人数")
	@TableField("register_count")
	private Integer registerCount;

	@ApiModelProperty(value = "活跃人数")
	@TableField("login_count")
	private Integer loginCount;

	@ApiModelProperty(value = "每日播放视频数")
	@TableField("video_view_count")
	private Integer videoViewCount;

	@ApiModelProperty(value = "每日新增课程数")
	@TableField("course_buy_count")
	private Integer courseBuyCount;

	@ApiModelProperty(value = "更新时间")
	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

	@ApiModelProperty(value = "创建时间")
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;


}
