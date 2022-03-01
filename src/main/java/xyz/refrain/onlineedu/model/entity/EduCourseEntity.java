package xyz.refrain.onlineedu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import xyz.refrain.onlineedu.model.enums.CourseStatusEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程
 * </p>
 *
 * @author Myles Yang
 * @since 2021-01-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("edu_course")
@ApiModel(value = "EduCourseEntity对象", description = "课程")
public class EduCourseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "课程ID")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "课程讲师ID")
	@TableField("teacher_id")
	private Integer teacherId;

	@ApiModelProperty(value = "课程专业ID")
	@TableField("subject_id")
	private Integer subjectId;

	@ApiModelProperty(value = "课程标题")
	@TableField("title")
	private String title;

	@ApiModelProperty(value = "课程销售价格，设置为0则可免费观看")
	@TableField("price")
	private Double price;

	@ApiModelProperty(value = "总课时")
	@TableField("lesson_num")
	private Integer lessonNum;

	@ApiModelProperty(value = "课程封面图片路径")
	@TableField("cover")
	private String cover;

	@ApiModelProperty(value = "课程描述")
	@TableField("description")
	private String description;

	@ApiModelProperty(value = "销售数量")
	@TableField("buy_count")
	private Integer buyCount;

	@ApiModelProperty(value = "浏览数量")
	@TableField("view_count")
	private Integer viewCount;

	@ApiModelProperty(value = "课程状态，草稿 审核 发表")
	@TableField("status")
	private CourseStatusEnum status;

	@ApiModelProperty(value = "上架下架，0下架 1上架")
	@TableField("enable")
	private Boolean enable;

	@ApiModelProperty(value = "排序")
	@TableField("sort")
	private Integer sort;

	@ApiModelProperty(value = "备注")
	@TableField("remarks")
	private String remarks;

	@ApiModelProperty(value = "更新时间")
	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

	@ApiModelProperty(value = "创建时间")
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;


}
