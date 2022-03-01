package xyz.refrain.onlineedu.model.vo.admin;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import xyz.refrain.onlineedu.model.base.BeanConvert;
import xyz.refrain.onlineedu.model.base.ValidGroupType;
import xyz.refrain.onlineedu.model.enums.CourseStatusEnum;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 课程 VO
 *
 * @author Myles Yang
 */
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class EduCourseSimpleVO implements Serializable, BeanConvert {

	private static final long serialVersionUID = 7600489047565033972L;

	@NotNull(groups = {ValidGroupType.Update.class}, message = "ID不能为空")
	@Min(value = 1, groups = {ValidGroupType.Update.class}, message = "ID需大于1")
	@ApiModelProperty(value = "课程ID")
	private Integer id;

	@NotNull(groups = {ValidGroupType.Save.class}, message = "讲师ID不能为空")
	@Min(value = 1, groups = {ValidGroupType.Save.class}, message = "讲师ID需大于1")
	@ApiModelProperty(value = "课程讲师ID")
	private Integer teacherId;

	private String teacherName;

	@NotNull(groups = {ValidGroupType.Save.class, ValidGroupType.Update.class}, message = "请选择分类")
	@Min(value = 1, groups = {ValidGroupType.Save.class, ValidGroupType.Update.class}, message = "分类ID需大于1")
	@ApiModelProperty(value = "课程专业ID")
	private Integer subjectId;

	@ApiModelProperty(value = "父分类")
	private EduSubjectSimpleParentVO subjectParent;

	@NotEmpty(groups = {ValidGroupType.Update.class, ValidGroupType.Save.class}, message = "标题不能为空")
	@Length(max = 63, groups = {ValidGroupType.Update.class, ValidGroupType.Save.class}, message = "标题长度不能超过63个字符")
	@ApiModelProperty(value = "课程标题")
	private String title;

	@ApiModelProperty(value = "课程销售价格，设置为0则可免费观看")
	private Double price;

	@ApiModelProperty(value = "总课时")
	private Integer lessonNum;

	@ApiModelProperty(value = "课程封面图片路径")
	private String cover;

	@ApiModelProperty(value = "销售数量")
	private Integer buyCount;

	@ApiModelProperty(value = "浏览数量")
	private Integer viewCount;

	@ApiModelProperty(value = "课程状态，草稿 审核 发表")
	private CourseStatusEnum status;

	@ApiModelProperty(value = "上架下架，0下架 1上架")
	private Boolean enable;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "备注")
	private String remarks;

}
