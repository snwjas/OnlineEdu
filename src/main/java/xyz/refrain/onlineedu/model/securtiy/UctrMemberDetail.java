package xyz.refrain.onlineedu.model.securtiy;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import xyz.refrain.onlineedu.model.base.BeanConvert;
import xyz.refrain.onlineedu.model.enums.SexEnum;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 用于安全验证的保存的用户信息（session）
 *
 * @author Myles Yang
 */
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class UctrMemberDetail implements Serializable, BeanConvert {

	private static final long serialVersionUID = 8511102543213811902L;

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

	@NotBlank(message = "昵称不能为空")
	@Length(max = 31)
	private String nickname;

	@ApiModelProperty(value = "性别 0 保密 1 女，2 男")
	private SexEnum sex;

	@Min(0)
	@Max(150)
	private Integer age;

	@Length(max = 127)
	private String avatar;

	private String sign;

	private Boolean enable;

	private String token;

}
