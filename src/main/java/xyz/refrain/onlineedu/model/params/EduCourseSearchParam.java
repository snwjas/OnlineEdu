package xyz.refrain.onlineedu.model.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import xyz.refrain.onlineedu.model.enums.CourseStatusEnum;

/**
 * 课程搜索参数
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@ApiModel("课程搜索参数")
public class EduCourseSearchParam extends BasePageParam {

	@ApiModelProperty(value = "课程讲师ID")
	private Integer teacherId;

	@ApiModelProperty(value = "课程专业ID")
	private Integer subjectId;

	@ApiModelProperty(value = "课程标题")
	private String title;

	@ApiModelProperty(value = "课程销售价格，设置为0则可免费观看")
	private Boolean free;

	@ApiModelProperty(value = "课程状态，草稿 审核 发表")
	private CourseStatusEnum status;

	@ApiModelProperty(value = "上架下架，0下架 1上架")
	private Boolean enable;
}
