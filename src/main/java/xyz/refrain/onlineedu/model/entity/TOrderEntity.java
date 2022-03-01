package xyz.refrain.onlineedu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import xyz.refrain.onlineedu.model.enums.PayTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单
 * </p>
 *
 * @author Myles Yang
 * @since 2021-01-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_order")
@ApiModel(value = "TOrderEntity对象", description = "订单")
public class TOrderEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "订单号(datetime+unsigned int)")
	@TableField("order_no")
	private String orderNo;

	@ApiModelProperty(value = "课程id")
	@TableField("course_id")
	private Integer courseId;

	@ApiModelProperty(value = "会员id")
	@TableField("member_id")
	private Integer memberId;

	@ApiModelProperty(value = "订单金额（分）")
	@TableField("total_fee")
	private Double totalFee;

	@ApiModelProperty(value = "支付类型（0：未支付 1：微信 2：支付宝）")
	@TableField("pay_type")
	private PayTypeEnum payType;

	@ApiModelProperty(value = "交易成功的流水号")
	@TableField("transaction_num")
	private String transactionNum;

	@ApiModelProperty(value = "支付完成时间")
	@TableField("pay_time")
	private LocalDateTime payTime;

	@ApiModelProperty(value = "更新时间")
	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

	@ApiModelProperty(value = "创建时间")
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;

}
