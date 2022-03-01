package xyz.refrain.onlineedu.model.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Base Page Param
 *
 * @author Myles Yang
 */
@Data
@Accessors(chain = true)
@ApiModel("基础分页参数")
public class BasePageParam {

	@Min(1)
	@ApiModelProperty("当前页")
	private long current;

	@Min(1)
	@Max(50)
	@ApiModelProperty("每页数量")
	private long pageSize;

}
