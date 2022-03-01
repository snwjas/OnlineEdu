package xyz.refrain.onlineedu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import xyz.refrain.onlineedu.model.enums.MessageRoleEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 消息表
 * </p>
 *
 * @author snwjas
 * @since 2021-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_message")
@ApiModel(value="SysMessageEntity对象", description="消息表")
public class SysMessageEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "消息id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "发送者Id")
    @TableField("from_id")
    private Integer fromId;

    @ApiModelProperty(value = "发送者角色(管理员、讲师...)")
    @TableField("from_role")
    private MessageRoleEnum fromRole;

    @ApiModelProperty(value = "接受者id")
    @TableField("to_id")
    private Integer toId;

    @ApiModelProperty(value = "接受者角色(教师、学员...)")
    @TableField("to_role")
    private MessageRoleEnum toRole;

    @ApiModelProperty(value = "消息标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "消息内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "是否已读(0未读 1已读)")
    @TableField("has_read")
    private Boolean hasRead;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;


}
