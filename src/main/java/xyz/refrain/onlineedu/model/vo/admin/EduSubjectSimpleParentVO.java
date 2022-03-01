package xyz.refrain.onlineedu.model.vo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import xyz.refrain.onlineedu.model.base.BeanConvert;

import java.io.Serializable;

/**
 * 课程科目(分类) vo
 *
 * @author Myles Yang
 */
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class EduSubjectSimpleParentVO implements Serializable, BeanConvert {

	private static final long serialVersionUID = 8905547578201436377L;

	private Integer id;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "父分类")
	private EduSubjectSimpleParentVO parent;

}
