package xyz.refrain.onlineedu.model.securtiy;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import xyz.refrain.onlineedu.model.base.BeanConvert;
import xyz.refrain.onlineedu.model.base.ValidGroupType;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用于安全验证的保存的用户信息（session）
 *
 * @author Myles Yang
 */
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class AclUserDetail implements Serializable, BeanConvert {

	private static final long serialVersionUID = 7586351918949128658L;

	private Integer id;

	@NotNull(groups = {ValidGroupType.Save.class}, message = "用户名不能为空")
	@Length(max = 31, message = "用户名长度不能超过31", groups = {ValidGroupType.Save.class})
	private String username;

	@NotNull(groups = {ValidGroupType.Save.class})
	@Length(max = 63, message = "密码长度不能超过63位", groups = {ValidGroupType.Save.class})
	private String password;

	@Length(max = 31, message = "昵称长度不能超过31")
	private String nickname;

	@Length(max = 255, message = "备注长度不能超过255")
	private String mark;

	private String avatar;

	@Length(max = 255, message = "个性签名长度不能超过255")
	private String sign;

	@NotNull(groups = {ValidGroupType.Save.class})
	private Integer roleId;

	private Boolean enable;

	private String token;

}
