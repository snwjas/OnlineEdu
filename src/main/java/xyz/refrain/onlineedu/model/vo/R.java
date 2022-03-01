package xyz.refrain.onlineedu.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一响应体
 *
 * @author Myles Yang
 */
@Data
@ApiModel("统一响应结果")
public class R implements Serializable {

	private static final long serialVersionUID = -637415113996231595L;

	@ApiModelProperty("响应时间")
	private LocalDateTime timestamp;

	@ApiModelProperty("响应状态值")
	private int status;

	/**
	 * 响应消息
	 */
	@ApiModelProperty("响应消息")
	private String message;

	@ApiModelProperty("响应数据")
	private Object data;


	public R(int status, String message, Object data) {
		this.timestamp = LocalDateTime.now();
		this.status = status;
		this.message = message;
		this.data = data;
	}

	public R(int status, String message) {
		this(status, message, null);
	}

	public R() {
	}
}
