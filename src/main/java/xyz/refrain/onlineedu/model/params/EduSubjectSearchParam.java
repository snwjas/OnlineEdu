package xyz.refrain.onlineedu.model.params;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 课程分类搜索参数
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@ApiModel("课程分类搜索参数")
public class EduSubjectSearchParam extends BasePageParam {

	private Integer parentId;

	private String title;

	private Boolean enable;

}
