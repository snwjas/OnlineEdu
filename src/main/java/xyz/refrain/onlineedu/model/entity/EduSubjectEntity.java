package xyz.refrain.onlineedu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程科目分类
 * </p>
 *
 * @author Myles Yang
 * @since 2021-01-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("edu_subject")
@ApiModel(value = "EduSubjectEntity对象", description = "课程科目")
public class EduSubjectEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "课程类别ID")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "类别名称")
	@TableField("title")
	private String title;

	@ApiModelProperty(value = "父ID")
	@TableField("parent_id")
	private Integer parentId;

	@ApiModelProperty(value = "排序字段")
	@TableField("sort")
	private Integer sort;

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
