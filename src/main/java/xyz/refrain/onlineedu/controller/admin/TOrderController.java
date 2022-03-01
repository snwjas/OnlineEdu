package xyz.refrain.onlineedu.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.refrain.onlineedu.model.params.TOrderSearchParam;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.service.TOrderService;

import javax.validation.Valid;

/**
 * 订单控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminTOrderController")
@RequestMapping("/api/admin/order")
@Api(value = "后台首页订单控制器", tags = {"后台首页订单接口"})
public class TOrderController {

	@Autowired
	private TOrderService tOrderService;

	@PostMapping("/list")
	@ApiOperation("搜索订单")
	public R list(@RequestBody @Valid TOrderSearchParam param) {
		return tOrderService.list(param);
	}

}
