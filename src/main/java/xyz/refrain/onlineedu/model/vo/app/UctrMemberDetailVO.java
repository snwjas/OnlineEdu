package xyz.refrain.onlineedu.model.vo.app;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * Member VO
 *
 * @author Myles Yang
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
public class UctrMemberDetailVO extends UctrMemberSimpleVO {

	private static final long serialVersionUID = -6507297250332935599L;

	private String avatar;

	@Length(max = 127)
	private String sign;

	private String token;

}
