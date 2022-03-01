package xyz.refrain.onlineedu.model.vo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import xyz.refrain.onlineedu.model.base.BeanConvert;
import xyz.refrain.onlineedu.model.base.ValidGroupType;
import xyz.refrain.onlineedu.model.enums.PayTypeEnum;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单视图
 *
 * @author Myles Yang
 */
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class TOrderVO implements Serializable, BeanConvert {

	private static final long serialVersionUID = 3611684608730777582L;

	@NotNull(groups = {ValidGroupType.Update.class}, message = "ID不能为空")
	@Min(value = 1, groups = {ValidGroupType.Update.class}, message = "ID需大于1")
	private Integer id;

	private String orderNo;

	@NotNull(message = "课程Id不能为空")
	@ApiModelProperty(value = "课程id")
	private Integer courseId;

	@NotNull(message = "会员Id不能为空")
	@ApiModelProperty(value = "会员id")
	private Integer memberId;

	@NotNull(message = "订单金额不能为空")
	@ApiModelProperty(value = "订单金额（元）")
	private Double totalFee;

	@ApiModelProperty(value = "支付类型（0：未支付 1：微信 2：支付宝）")
	private PayTypeEnum payType;

	@ApiModelProperty(value = "支付完成时间")
	private LocalDateTime payTime;

	@ApiModelProperty(value = "交易成功的流水号")
	private String transactionNum;

	@ApiModelProperty(value = "创建时间")
	private LocalDateTime createTime;

}
