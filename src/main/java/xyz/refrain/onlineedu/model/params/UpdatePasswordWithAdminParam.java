package xyz.refrain.onlineedu.model.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Update Password With Admin Param
 *
 * @author Myles Yang
 */
@Data
@ApiModel("管理员权限更新密码参数")
public class UpdatePasswordWithAdminParam {

	@NotNull
	@Min(value = 1L, message = "用户id大于1")
	@ApiModelProperty("用户id")
	private Integer userId;

	@NotBlank(message = "新密码不能为空")
	@Length(max = 63, message = "密码长度不能超过63位")
	@ApiModelProperty("新密码")
	private String newPassword;

}
