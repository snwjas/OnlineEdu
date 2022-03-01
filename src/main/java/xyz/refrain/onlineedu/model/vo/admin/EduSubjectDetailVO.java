package xyz.refrain.onlineedu.model.vo.admin;

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
import java.util.List;

/**
 * 课程科目(分类) vo
 *
 * @author Myles Yang
 */
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class EduSubjectDetailVO implements Serializable, BeanConvert {

	private static final long serialVersionUID = 8171974963430070292L;

	@NotNull(groups = {ValidGroupType.Update.class}, message = "ID不能为空")
	@Min(value = 1, groups = {ValidGroupType.Update.class}, message = "ID需大于1")
	@ApiModelProperty(value = "ID")
	private Integer id;

	@ApiModelProperty(value = "父ID")
	private Integer parentId;

	@NotEmpty(groups = {ValidGroupType.Update.class, ValidGroupType.Save.class}, message = "标题不能为空")
	@Length(max = 63, groups = {ValidGroupType.Update.class, ValidGroupType.Save.class}, message = "标题长度不能超过63个字符")
	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "是否启用，0否1是")
	private Boolean enable;

	@ApiModelProperty(value = "子分类")
	private List<EduSubjectDetailVO> children;

}
