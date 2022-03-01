package xyz.refrain.onlineedu.model.vo.teacher;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import xyz.refrain.onlineedu.model.base.BeanConvert;
import xyz.refrain.onlineedu.model.base.ValidGroupType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 章节 VO （临时表，二次修改）
 *
 * @author Myles Yang
 */
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class EduChapterVO implements Serializable, BeanConvert {

	private static final long serialVersionUID = 8235402058856062220L;

	@NotNull(groups = {ValidGroupType.Update.class}, message = "ID不能为空")
	@Min(value = 1, groups = {ValidGroupType.Update.class}, message = "ID需大于1")
	@ApiModelProperty(value = "ID")
	private Integer id;

	@NotNull(groups = {ValidGroupType.Save.class}, message = "课程ID不能为空")
	@Min(value = 1, groups = {ValidGroupType.Save.class}, message = "课程ID需大于1")
	@ApiModelProperty(value = "课程ID")
	private Integer courseId;

	@NotEmpty(groups = {ValidGroupType.Update.class, ValidGroupType.Save.class}, message = "章节名称不能为空")
	@Length(max = 63, groups = {ValidGroupType.Update.class, ValidGroupType.Save.class}, message = "章节名称长度不能超过63个字符")
	@ApiModelProperty(value = "章节名称")
	private String title;

	@ApiModelProperty(value = "排序")
	private Integer sort;

}
