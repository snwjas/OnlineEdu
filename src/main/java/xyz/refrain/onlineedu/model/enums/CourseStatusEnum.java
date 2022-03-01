package xyz.refrain.onlineedu.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * Course Status
 *
 * @author Myles Yang
 */
public enum CourseStatusEnum {

	/**
	 * 草稿
	 */
	DRAFT(0),

	/**
	 * 已发布
	 */
	PUBLISH(1),

	/**
	 * 驳回
	 */
	TURN_DOWN(2),

	/**
	 * 审核，包含（初审和二审核）
	 */
	AUDITING(3),

	/**
	 * 审核中，首次审
	 */
	FIRST_AUDITING(4),

	/**
	 * 二次审核中，再次修改的内容
	 */
	SECOND_AUDITING(5),


	;


	@EnumValue
	@Getter
	private final int value;

	CourseStatusEnum(int value) {
		this.value = value;
	}
}
