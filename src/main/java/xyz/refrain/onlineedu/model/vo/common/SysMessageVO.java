package xyz.refrain.onlineedu.model.vo.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import xyz.refrain.onlineedu.model.base.BeanConvert;
import xyz.refrain.onlineedu.model.enums.MessageRoleEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * todo
 *
 * @author Myles Yang
 */
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class SysMessageVO implements Serializable, BeanConvert {

	private static final long serialVersionUID = -7969573963163929230L;

	@ApiModelProperty(value = "消息id")
	private Integer id;

	@ApiModelProperty(value = "发送者角色(管理员、讲师...)")
	private MessageRoleEnum fromRole;

	@ApiModelProperty(value = "发送者Id")
	private Integer fromId;

	private String fromName;

	@ApiModelProperty(value = "接受者角色(教师、学员...)")
	private MessageRoleEnum toRole;

	@ApiModelProperty(value = "接受者id")
	private Integer toId;

	@ApiModelProperty(value = "消息标题")
	private String title;

	@ApiModelProperty(value = "消息内容")
	private String content;

	@ApiModelProperty(value = "是否已读(0未读 1已读)")
	private Boolean read;

	@ApiModelProperty(value = "创建时间")
	private LocalDateTime createTime;
}
