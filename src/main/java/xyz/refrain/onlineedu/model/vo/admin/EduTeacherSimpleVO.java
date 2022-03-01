package xyz.refrain.onlineedu.model.vo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import xyz.refrain.onlineedu.model.base.BeanConvert;
import xyz.refrain.onlineedu.model.enums.TeacherStatusEnum;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * Edu Teacher Simple VO
 *
 * @author Myles Yang
 */

@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class EduTeacherSimpleVO implements Serializable, BeanConvert {

	private static final long serialVersionUID = -2830795242128760005L;

	@NotNull(message = "用户ID不能为空")
	@Min(value = 1, message = "用户ID必须大于1")
	private Integer id;

	@NotBlank(message = "手机号不能为空")
	@Pattern(regexp = "^0?(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57])[0-9]{8}$", message = "手机号不合法")
	@ApiModelProperty(value = "手机号")
	private String mobile;

	@NotBlank(message = "邮箱地址不能为空")
	@Email(message = "邮箱地址不合法")
	@ApiModelProperty(value = "邮箱地址")
	private String email;

	@NotBlank(message = "讲师名称不能为空")
	@Length(max = 31, message = "讲师名称不能超过31个字符")
	@ApiModelProperty(value = "讲师姓名")
	private String name;

	@ApiModelProperty(value = "讲师简历")
	private String resume;

	@Min(0)
	@Max(100)
	@ApiModelProperty(value = "分成比例，0-100")
	private Integer division;

	@Min(Integer.MIN_VALUE)
	@Max(Integer.MAX_VALUE)
	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "是否启用，0否1是")
	private Boolean enable;

	@ApiModelProperty(value = "讲师状态：审核通过；审核不通过；待审核")
	private TeacherStatusEnum status;

}
