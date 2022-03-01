package xyz.refrain.onlineedu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import xyz.refrain.onlineedu.model.enums.TeacherStatusEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 讲师
 * </p>
 *
 * @author Myles Yang
 * @since 2021-01-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("edu_teacher")
@ApiModel(value="EduTeacherEntity对象", description="讲师")
public class EduTeacherEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "讲师ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "手机号")
    @TableField("mobile")
    private String mobile;

    @ApiModelProperty(value = "邮箱地址")
    @TableField("email")
    private String email;

    @ApiModelProperty(value = "密码")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "讲师姓名")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "讲师简介")
    @TableField("intro")
    private String intro;

    @ApiModelProperty(value = "讲师头像")
    @TableField("avatar")
    private String avatar;

    @ApiModelProperty(value = "讲师简历")
    @TableField("resume")
    private String resume;

    @ApiModelProperty(value = "分成比例，0-100")
    @TableField("division")
    private int division;

    @ApiModelProperty(value = "排序")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty(value = "是否启用，0否1是")
    @TableField("enable")
    private Boolean enable;

    @ApiModelProperty(value = "讲师状态：审核通过；审核不通过；待审核")
    @TableField("status")
    private TeacherStatusEnum status;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;


}
