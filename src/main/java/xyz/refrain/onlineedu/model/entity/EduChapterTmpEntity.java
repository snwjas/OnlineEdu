package xyz.refrain.onlineedu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程章节临时表（用于存放二次修改的数据）
 * </p>
 *
 * @author snwjas
 * @since 2021-05-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("edu_chapter_tmp")
@ApiModel(value="EduChapterTmpEntity对象", description="课程章节临时表（用于存放二次修改的数据）")
public class EduChapterTmpEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "原章节ID")
    @TableField("oid")
    private Integer oid;

    @ApiModelProperty(value = "课程ID")
    @TableField("course_id")
    private Integer courseId;

    @ApiModelProperty(value = "章节名称")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "显示排序")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;


}
