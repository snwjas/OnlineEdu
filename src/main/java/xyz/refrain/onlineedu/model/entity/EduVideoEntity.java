package xyz.refrain.onlineedu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程视频
 * </p>
 *
 * @author Myles Yang
 * @since 2021-01-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("edu_video")
@ApiModel(value = "EduVideoEntity对象", description = "课程视频")
public class EduVideoEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "视频ID")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "课程ID")
	@TableField("course_id")
	private Integer courseId;

	@ApiModelProperty(value = "章节ID")
	@TableField("chapter_id")
	private Integer chapterId;

	@ApiModelProperty(value = "视频名称")
	@TableField("title")
	private String title;

	@ApiModelProperty(value = "云端视频资源")
	@TableField("video_id")
	private String videoId;

	@ApiModelProperty(value = "排序字段")
	@TableField("sort")
	private Integer sort;

	@ApiModelProperty(value = "播放次数")
	@TableField("play_count")
	private Integer playCount;

	@ApiModelProperty(value = "是否可以试听：0免费 1收费")
	@TableField("free")
	private Boolean free;

	@ApiModelProperty(value = "视频时长（秒）")
	@TableField("duration")
	private String duration;

	@ApiModelProperty(value = "视频源文件大小（字节）")
	@TableField("size")
	private Long size;

	@ApiModelProperty(value = "更新时间")
	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

	@ApiModelProperty(value = "创建时间")
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;


}
