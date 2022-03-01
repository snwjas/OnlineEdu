package xyz.refrain.onlineedu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import xyz.refrain.onlineedu.model.enums.SexEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 会员表
 * </p>
 *
 * @author Myles Yang
 * @since 2021-01-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("uctr_member")
@ApiModel(value = "UctrMemberEntity对象", description = "会员表")
public class UctrMemberEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "会员id")
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

	@ApiModelProperty(value = "昵称")
	@TableField("nickname")
	private String nickname;

	@ApiModelProperty(value = "性别 0 保密 1 女，2 男")
	@TableField("sex")
	private SexEnum sex;

	@ApiModelProperty(value = "年龄")
	@TableField("age")
	private Integer age;

	@ApiModelProperty(value = "用户头像")
	@TableField("avatar")
	private String avatar;

	@ApiModelProperty(value = "用户签名")
	@TableField("sign")
	private String sign;

	@ApiModelProperty(value = "是否启用，0否1是")
	@TableField("enable")
	private Boolean enable;

	@ApiModelProperty(value = "更新时间")
	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

	@ApiModelProperty(value = "创建时间")
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;


}
