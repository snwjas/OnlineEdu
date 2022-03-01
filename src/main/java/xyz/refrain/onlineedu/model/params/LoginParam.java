package xyz.refrain.onlineedu.model.params;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class LoginParam {

	@NotNull(message = "用户名不能为空")
	@Length(max = 31, message = "用户名太长了")
	private String username;

	@NotNull(message = "密码不能为空")
	@Length(min = 6, max = 31, message = "密码长度应在6-31个字符")
	private String password;
}
