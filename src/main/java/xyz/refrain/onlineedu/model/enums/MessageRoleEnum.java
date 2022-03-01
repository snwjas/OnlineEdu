package xyz.refrain.onlineedu.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 消息角色表
 *
 * @author Myles Yang
 */
public enum MessageRoleEnum {

	/**
	 * 发送者：管理员
	 */
	FROM_ADMIN(1),

	/**
	 * 发送者：讲师
	 */
	FROM_TEACHER(2),

	/**
	 * 接受者：讲师
	 */
	TO_TEACHER(3),

	/**
	 * 接受者：学员
	 */
	TO_STUDENT(4);

	@EnumValue
	@Getter
	private final int value;

	MessageRoleEnum(int value) {
		this.value = value;
	}
}
