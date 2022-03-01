package xyz.refrain.onlineedu.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 讲师状态
 *
 * @author Myles Yang
 */
public enum TeacherStatusEnum {

	/**
	 * 审核通过
	 */
	PASS(0),

	/**
	 * 审核中
	 */
	AUDITING(1),

	/**
	 * 审核不通过
	 */
	NOT_PASS(2);


	@EnumValue
	@Getter
	private final int value;

	TeacherStatusEnum(int value) {
		this.value = value;
	}
}
