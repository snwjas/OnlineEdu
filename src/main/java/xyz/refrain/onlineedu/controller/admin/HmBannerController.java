package xyz.refrain.onlineedu.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.model.base.ValidGroupType;
import xyz.refrain.onlineedu.model.params.HmBannerSearchParam;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.admin.HmBannerVO;
import xyz.refrain.onlineedu.service.HmBannerService;
import xyz.refrain.onlineedu.utils.RUtils;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.Objects;

/**
 * Banner控制器
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminHmBannerController")
@RequestMapping("/api/admin/banner")
@Api(value = "后台首页Banner控制器", tags = {"后台首页Banner接口"})
public class HmBannerController {

	@Autowired
	private HmBannerService hmBannerService;

	@PostMapping("/list")
	@ApiOperation("搜索Banner")
	public R list(@RequestBody @Valid HmBannerSearchParam param) {
		return hmBannerService.list(param);
	}

	@PostMapping("/create")
	@ApiOperation("创建Banner")
	public R create(@Validated(ValidGroupType.Save.class) HmBannerVO vo,
	                @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
		if (Objects.isNull(file)) {
			return RUtils.fail("海报图片不能为空");
		}
		return hmBannerService.create(vo, file);
	}

	@PostMapping("/update")
	@ApiOperation("修改Banner信息")
	public R updateProfile(@Validated(ValidGroupType.Update.class) HmBannerVO vo,
	                       @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
		return hmBannerService.update(vo, file);
	}

	@PostMapping("/refresh")
	@ApiOperation("刷新Banner缓存")
	public R refresh() {
		return hmBannerService.refresh();
	}

	@PostMapping("/disable/{bannerId}")
	@ApiOperation("禁用Banner")
	public R disable(@PathVariable("bannerId") @Min(1) Integer bannerId) {
		return hmBannerService.disable(bannerId);
	}

	@PostMapping("/enable/{bannerId}")
	@ApiOperation("启用Banner")
	public R enable(@PathVariable("bannerId") @Min(1) Integer bannerId) {
		return hmBannerService.enable(bannerId);
	}

	@PostMapping("/delete/{bannerId}")
	@ApiOperation("删除Banner")
	public R delete(@PathVariable("bannerId") @Min(1) Integer bannerId) {
		return hmBannerService.delete(bannerId);
	}

}
