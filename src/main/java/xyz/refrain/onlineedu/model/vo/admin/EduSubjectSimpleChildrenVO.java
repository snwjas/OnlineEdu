package xyz.refrain.onlineedu.model.vo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import xyz.refrain.onlineedu.model.base.BeanConvert;

import java.io.Serializable;
import java.util.List;

/**
 * 课程科目(分类) vo
 *
 * @author Myles Yang
 */
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class EduSubjectSimpleChildrenVO implements Serializable, BeanConvert {

	private static final long serialVersionUID = 69236580018685954L;

	private Integer id;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "子分类")
	private List<EduSubjectSimpleChildrenVO> children;

}
