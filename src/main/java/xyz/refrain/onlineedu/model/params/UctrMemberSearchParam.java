package xyz.refrain.onlineedu.model.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * Uctr Member Search Param
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@ApiModel("学员搜索参数")
public class UctrMemberSearchParam extends BasePageParam {

	private String nickname;

	private String mobile;

	private Boolean enable;

	@ApiModelProperty("大于该注册时间")
	private LocalDateTime beginCreate;

	@ApiModelProperty("小于该注册时间")
	private LocalDateTime endCreate;

}
