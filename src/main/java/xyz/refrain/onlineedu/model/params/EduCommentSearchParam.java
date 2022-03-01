package xyz.refrain.onlineedu.model.params;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 评论搜索参数
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@ApiModel("评论搜索参数")
public class EduCommentSearchParam extends BasePageParam {

	private Integer courseId;

	private Boolean status;

}
