package xyz.refrain.onlineedu.model.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import xyz.refrain.onlineedu.model.enums.PayTypeEnum;

import java.time.LocalDateTime;

/**
 * 订单搜索参数
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@ApiModel("订单搜索参数")
public class TOrderSearchParam extends BasePageParam {

	private Integer memberId;

	private String orderNo;

	private PayTypeEnum payType;

	@ApiModelProperty("大于该订单创建时间")
	private LocalDateTime beginCreate;

	@ApiModelProperty("小于该订单创建时间")
	private LocalDateTime endCreate;

}
