package xyz.refrain.onlineedu.model.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * Update Password Param
 *
 * @author Myles Yang
 */
@Data
@ApiModel("密码更新参数")
public class UpdatePasswordParam {

	@NotBlank(message = "原密码不能为空")
	@Length(max = 63, message = "密码长度不能超过63位")
	@ApiModelProperty("原密码")
	private String oldPassword;

	@NotBlank(message = "新密码不能为空")
	@Length(max = 63, message = "密码长度不能超过63位")
	@ApiModelProperty("新密码")
	private String newPassword;

	@NotBlank(message = "确认的新密码不能为空")
	@Length(max = 63, message = "密码长度不能超过63位")
	@ApiModelProperty("确认的新密码")
	private String confirmNewPassword;

}
