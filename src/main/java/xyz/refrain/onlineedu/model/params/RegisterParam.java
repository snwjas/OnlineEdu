package xyz.refrain.onlineedu.model.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Accessors(chain = true)
public class RegisterParam {

	@NotNull(message = "手机号不能为空")
	@Pattern(regexp = "^0?(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57])[0-9]{8}$", message = "手机号不合法")
	private String username;

	@NotBlank(message = "密码不能为空")
	@Length(max = 63, message = "密码长度不能超过63位")
	@ApiModelProperty("密码")
	private String password;

	@NotBlank(message = "确认的新密码不能为空")
	@Length(max = 63, message = "密码长度不能超过63位")
	@ApiModelProperty("确认的新密码")
	private String confirmPassword;
}
