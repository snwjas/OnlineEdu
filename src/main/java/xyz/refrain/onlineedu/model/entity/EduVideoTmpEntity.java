package xyz.refrain.onlineedu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程视频表（用于存放二次修改的数据）
 * </p>
 *
 * @author snwjas
 * @since 2021-05-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("edu_video_tmp")
@Accessors(chain = true)
@ApiModel(value="EduVideoTmpEntity对象", description="课程视频表（用于存放二次修改的数据）")
public class EduVideoTmpEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "原视频ID")
    @TableField("oid")
    private Integer oid;

    @ApiModelProperty(value = "课程ID")
    @TableField("course_id")
    private Integer courseId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "章节ID")
    @TableField("chapter_id")
    private Long chapterId;

    @ApiModelProperty(value = "视频显示名称")
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
