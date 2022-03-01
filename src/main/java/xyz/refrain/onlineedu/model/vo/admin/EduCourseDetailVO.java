package xyz.refrain.onlineedu.model.vo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import xyz.refrain.onlineedu.model.base.ValidGroupType;

import javax.validation.constraints.NotEmpty;

/**
 * 课程 VO
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class EduCourseDetailVO extends  EduCourseSimpleVO{

	private static final long serialVersionUID = 7600489047565033972L;

	@NotEmpty(groups = {ValidGroupType.Update.class, ValidGroupType.Save.class}, message = "课程简介不能为空")
	@ApiModelProperty(value = "课程描述")
	private String description;

}
