package xyz.refrain.onlineedu.model.vo.teacher;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import xyz.refrain.onlineedu.model.base.BeanConvert;
import xyz.refrain.onlineedu.model.base.ValidGroupType;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论 VO
 *
 * @author Myles Yang
 */
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class EduCommentVO implements Serializable, BeanConvert {

	private static final long serialVersionUID = -3731515228575891846L;

	@NotNull(groups = {ValidGroupType.Update.class}, message = "ID不能为空")
	@Min(value = 1, groups = {ValidGroupType.Update.class}, message = "ID需大于1")
	@ApiModelProperty(value = "评论ID")
	private Integer id;

	@NotNull(groups = {ValidGroupType.Save.class}, message = "课程ID不能为空")
	@Min(value = 1, groups = {ValidGroupType.Save.class}, message = "课程ID需大于1")
	@ApiModelProperty(value = "课程id")
	private Integer courseId;

	private String courseName;

	@NotNull(groups = {ValidGroupType.Save.class}, message = "讲师ID不能为空")
	@Min(value = 1, groups = {ValidGroupType.Save.class}, message = "讲师ID需大于1")
	@ApiModelProperty(value = "讲师id")
	private Integer teacherId;

	@NotNull(groups = {ValidGroupType.Save.class}, message = "学员ID不能为空")
	@Min(value = 1, groups = {ValidGroupType.Save.class}, message = "学员ID需大于1")
	@ApiModelProperty(value = "会员id")
	private Integer memberId;

	private String memberName;

	private String memberAvatar;

	@NotEmpty(groups = {ValidGroupType.Update.class, ValidGroupType.Save.class}, message = "评论内容不能为空")
	@Length(max = 1023, groups = {ValidGroupType.Update.class, ValidGroupType.Save.class},
			message = "评论内容长度不能超过1023个字符")
	@ApiModelProperty(value = "评论内容")
	private String content;

	@NotNull(groups = {ValidGroupType.Save.class}, message = "请评分")
	@Min(1)
	@Max(5)
	@ApiModelProperty(value = "评分（满分5.00）")
	private Double mark;

	@ApiModelProperty(value = "评论状态 0审核中 1通过")
	private Boolean status;

	@ApiModelProperty(value = "创建时间")
	private LocalDateTime createTime;

}
