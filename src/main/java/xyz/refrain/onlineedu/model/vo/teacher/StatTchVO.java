package xyz.refrain.onlineedu.model.vo.teacher;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 讲师的统计字段
 *
 * @author Myles Yang
 */
@Accessors(chain = true)
@Data
public class StatTchVO implements Serializable {

	private static final long serialVersionUID = -6242784993489855119L;

	@ApiModelProperty("发表的课程数量")
	private Integer courseCount;

	@ApiModelProperty("发表的课程视频数量")
	private Integer videoCount;

	@ApiModelProperty("总评论数量")
	private Integer commentCount;

	@ApiModelProperty("待审核的评论数量")
	private Integer auditingCommentCount;

	@ApiModelProperty("加入平台的时间")
	private LocalDateTime joinDateTime;

	@ApiModelProperty("加入平台的天数")
	private Integer joinDaysCount;

}
