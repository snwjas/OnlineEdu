package xyz.refrain.onlineedu.model.params;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * AclUser Search Param
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@ApiModel("后台用户搜索参数")
public class AclUserSearchParam extends BasePageParam {

	private String username;

	private Integer roleId;

	private Boolean enable;

}
