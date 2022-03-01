package xyz.refrain.onlineedu.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 支付类
 *
 * @author Myles Yang
 */
public enum PayTypeEnum {

	/**
	 * 未支付
	 */
	NONE(0),

	/**
	 * 微信支付
	 */
	WECHAT_PAY(1),

	/**
	 * 支付宝
	 */
	ALI_PAY(2);

	@EnumValue
	@Getter
	private final int value;

	PayTypeEnum(int value) {
		this.value = value;
	}
}
