package xyz.refrain.onlineedu.model.vo.admin;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import xyz.refrain.onlineedu.model.base.BeanConvert;
import xyz.refrain.onlineedu.model.base.ValidGroupType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * User VO
 */
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class AclUserVO implements Serializable, BeanConvert {

	private static final long serialVersionUID = -938483463348743424L;

	@NotNull(groups = {ValidGroupType.Update.class})
	@Min(value = 1, groups = {ValidGroupType.Update.class})
	private Integer id;

	@Length(max = 31, message = "用户名长度不能超过31")
	private String username;

	@Length(max = 31, message = "昵称长度不能超过31")
	private String nickname;

	private String avatar;

	@Length(max = 255, message = "备注长度不能超过255")
	private String mark;

	@Length(max = 255, message = "个性签名长度不能超过255")
	private String sign;

	@NotNull(groups = {ValidGroupType.Save.class})
	private Integer roleId;

	private Boolean enable;

	private String token;

}
