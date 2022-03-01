package xyz.refrain.onlineedu.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * Sex Enum
 *
 * @author Myles Yang
 */
public enum SexEnum {

	/**
	 * 保密
	 */
	SECRET(0),

	/**
	 * 女性
	 */
	FEMALE(1),

	/**
	 * 男性
	 */
	MALE(2);

	@EnumValue
	@Getter
	private final int value;

	SexEnum(int value) {
		this.value = value;
	}
}
