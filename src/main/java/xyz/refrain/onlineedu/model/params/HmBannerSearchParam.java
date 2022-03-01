package xyz.refrain.onlineedu.model.params;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Uctr Member Search Param
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@ApiModel("首页Banner搜索参数")
public class HmBannerSearchParam extends BasePageParam {

	private String title;

	private Boolean enable;

}
