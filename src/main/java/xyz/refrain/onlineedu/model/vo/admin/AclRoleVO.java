package xyz.refrain.onlineedu.model.vo.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import xyz.refrain.onlineedu.model.base.BeanConvert;

import java.io.Serializable;

/**
 * Acl Role VO
 *
 * @author Myles Yang
 */
@ApiModel("角色视图对象")
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class AclRoleVO implements Serializable, BeanConvert {

	@ApiModelProperty(value = "角色id")
	private Integer id;

	@ApiModelProperty(value = "角色名称")
	private String name;

	@ApiModelProperty(value = "角色具有的权限ID串")
	private String permissionId;

	@ApiModelProperty(value = "是否启用，0否1是")
	private Boolean enable;

}
