package xyz.refrain.onlineedu.model.vo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 普通的网站统计字段信息
 *
 * @author Myles Yang
 */
@Accessors(chain = true)
@Data
public class StatCommonVO implements Serializable {

	private static final long serialVersionUID = 5068765125751156940L;

	// 人员

	@ApiModelProperty("管理员数量")
	private Integer adminCount;

	@ApiModelProperty("被禁用的管理员数量")
	private Integer disabledAdminCount;

	@ApiModelProperty("讲师数量")
	private Integer teacherCount;

	@ApiModelProperty("被禁用的讲师数量")
	private Integer disabledTeacherCount;

	@ApiModelProperty("学员数量")
	private Integer studentCount;

	@ApiModelProperty("被禁用的学员数量")
	private Integer disabledStudentCount;

	@ApiModelProperty("男性学员数量")
	private Integer MaleStudentCount;

	@ApiModelProperty("女性学员数量")
	private Integer femaleStudentCount;

	// 课程

	@ApiModelProperty("课程总数量")
	private Integer courseCount;

	@ApiModelProperty("被下架的课程数量")
	private Integer disabledCourseCount;

	@ApiModelProperty("上架的课程数量")
	private Integer enabledCourseCount;

	@ApiModelProperty("正在编辑的课程数量")
	private Integer editingCourseCount;

	@ApiModelProperty("正在审核的课程数量")
	private Integer auditingCourseCount;

	@ApiModelProperty("被驳回的课程数量")
	private Integer rejectedCourseCount;

	@ApiModelProperty("课程视频数量")
	private Integer videoCount;

	@ApiModelProperty("订单总数量")
	private Integer orderCount;

	@ApiModelProperty("微信支付订单总数量")
	private Integer orderPayByWechatCount;

	@ApiModelProperty("支付宝支付的订单数量")
	private Integer orderPayByAlipayCount;

	@ApiModelProperty("未完成的订单数量")
	private Integer orderPayByNoneCount;

}
