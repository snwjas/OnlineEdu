package xyz.refrain.onlineedu.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.service.AclRoleService;

/**
 * 角色控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminAclRoleController")
@RequestMapping("/api/admin/role")
@Api(value = "后台角色控制器", tags = {"后台角色接口"})
public class AclRoleController {

	@Autowired
	private AclRoleService aclRoleService;

	@GetMapping("/list/all/{enable}")
	@ApiOperation("根据获取所有角色")
	public R listAll(@PathVariable(value = "enable", required = false) Boolean enable) {
		return aclRoleService.listAll(enable);
	}

}
