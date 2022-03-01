package xyz.refrain.onlineedu.model.securtiy;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import xyz.refrain.onlineedu.model.base.BeanConvert;
import xyz.refrain.onlineedu.model.enums.TeacherStatusEnum;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 用于安全验证的保存的用户信息（session）
 *
 * @author Myles Yang
 */
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class EduTeacherDetail implements Serializable, BeanConvert {

	private static final long serialVersionUID = 7584974618137147474L;

	private Integer id;

	@NotBlank(message = "手机号不能为空")
	@Pattern(regexp = "^0?(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57])[0-9]{8}$", message = "手机号不合法")
	@ApiModelProperty(value = "手机号")
	private String mobile;

	@NotBlank(message = "邮箱地址不能为空")
	@Email(message = "邮箱地址不合法")
	@ApiModelProperty(value = "邮箱地址")
	private String email;

	@Length(max = 31)
	@ApiModelProperty(value = "密码")
	private String password;

	@NotBlank(message = "讲师名称不能为空")
	@Length(max = 31,message = "讲师名称不能超过31个字符")
	@ApiModelProperty(value = "讲师名称")
	private String name;

	@Length(max = 1023)
	@ApiModelProperty(value = "讲师简介")
	private String intro;

	@ApiModelProperty(value = "讲师头像")
	private String avatar;

	@ApiModelProperty(value = "讲师简历")
	private String resume;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "是否启用，0否1是")
	private Boolean enable;

	@ApiModelProperty(value = "讲师状态：审核通过；审核不通过；待审核")
	private TeacherStatusEnum status;

	private String token;

}
