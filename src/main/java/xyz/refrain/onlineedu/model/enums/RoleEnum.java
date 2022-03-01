package xyz.refrain.onlineedu.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * Role Enum
 *
 * @author Myles Yang
 */
public enum RoleEnum {

	/**
	 * 管理员
	 */
	ADMINISTRATOR(0),

	/**
	 * 讲师
	 */
	TEACHER(1);

	@EnumValue
	@Getter
	private final int value;

	RoleEnum(int value) {
		this.value = value;
	}
}
