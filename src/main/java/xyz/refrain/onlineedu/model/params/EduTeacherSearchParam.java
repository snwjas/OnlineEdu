package xyz.refrain.onlineedu.model.params;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import xyz.refrain.onlineedu.model.enums.TeacherStatusEnum;

/**
 * Edu Teacher Search Param
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@ApiModel("讲师搜索参数")
public class EduTeacherSearchParam extends BasePageParam {

	private String name;

	private String mobile;

	private Boolean enable;

	private TeacherStatusEnum status;

}
