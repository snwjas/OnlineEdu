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
 * 视频 VO
 *
 * @author Myles Yang
 */
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class EduVideoVO implements Serializable, BeanConvert {

	private static final long serialVersionUID = 3611684608730777582L;

	@NotNull(groups = {ValidGroupType.Update.class}, message = "ID不能为空")
	@Min(value = 1, groups = {ValidGroupType.Update.class}, message = "ID需大于1")
	private Integer id;

	@NotNull(groups = {ValidGroupType.Save.class}, message = "课程ID不能为空")
	@Min(value = 1, groups = {ValidGroupType.Save.class}, message = "课程ID需大于1")
	@ApiModelProperty(value = "课程id")
	private Integer courseId;

	@NotNull(groups = {ValidGroupType.Save.class, ValidGroupType.Update.class}, message = "章节ID不能为空")
	@Min(value = 1, groups = {ValidGroupType.Save.class, ValidGroupType.Update.class}, message = "章节ID需大于1")
	@ApiModelProperty(value = "章节ID")
	private Integer chapterId;

	@NotEmpty(groups = {ValidGroupType.Update.class, ValidGroupType.Save.class}, message = "视频名称不能为空")
	@Length(max = 63, groups = {ValidGroupType.Update.class, ValidGroupType.Save.class}, message = "视频名称长度不能超过63个字符")
	@ApiModelProperty(value = "视频名称")
	private String title;

	@ApiModelProperty(value = "云端视频资源")
	private String videoId;

	@ApiModelProperty(value = "排序字段")
	private Integer sort;

	@ApiModelProperty(value = "播放次数")
	private Integer playCount;

	@ApiModelProperty(value = "是否可以试听：0免费 1收费")
	private Boolean free;

	@ApiModelProperty(value = "视频时长（秒）")
	private String duration;

	@ApiModelProperty(value = "视频源文件大小（字节）")
	private Long size;
}
