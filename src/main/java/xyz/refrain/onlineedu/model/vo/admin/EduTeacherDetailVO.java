package xyz.refrain.onlineedu.model.vo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * Edu Teacher Detail VO
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class EduTeacherDetailVO extends EduTeacherSimpleVO {

	private static final long serialVersionUID = -2893578215520107306L;

	@ApiModelProperty(value = "讲师头像")
	private String avatar;

	@Length(max = 1023, message = "简介长度不能超过1023个字符")
	@ApiModelProperty(value = "讲师简介")
	private String intro;

	private String token;

}
