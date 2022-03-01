package xyz.refrain.onlineedu.model.vo.app;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import xyz.refrain.onlineedu.model.base.BeanConvert;
import xyz.refrain.onlineedu.model.enums.PayTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学员订单
 *
 * @author Myles Yang
 */
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class MemberOrderVO implements Serializable, BeanConvert {

	private static final long serialVersionUID = -6160895513916507465L;

	private String orderNo;

	private Integer courseId;

	private String courseName;

	private Double totalFee;

	private PayTypeEnum payType;

	private LocalDateTime createTime;

	private LocalDateTime payTime;

}
