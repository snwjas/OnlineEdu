package xyz.refrain.onlineedu.service;

import cn.hutool.core.io.FileTypeUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.constant.CacheKeyPrefix;
import xyz.refrain.onlineedu.mapper.HmBannerMapper;
import xyz.refrain.onlineedu.model.entity.HmBannerEntity;
import xyz.refrain.onlineedu.model.params.HmBannerSearchParam;
import xyz.refrain.onlineedu.model.vo.PageResult;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.admin.HmBannerVO;
import xyz.refrain.onlineedu.utils.RUtils;
import xyz.refrain.onlineedu.utils.RedisUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 首页 banner 服务
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class HmBannerService {

	@Resource
	private HmBannerMapper hmBannerMapper;

	@Autowired
	private AliyunOssService aliyunOssService;

	/**
	 * 互斥锁
	 */
	private final Lock lock = new ReentrantLock();

	/**
	 * 获取Banner数据（从缓存中获取，如果获取失败从数据库中取，然后刷新缓存）
	 */
	public R get() {
		Object bannerList = RedisUtils.get(CacheKeyPrefix.CACHE_BANNER);
		if (Objects.isNull(bannerList)) {
			// 缓存中没有数据，去数据库取并设置到缓存去
			// 使用互斥锁，只能一个线程取，其他的返回空值
			if (lock.tryLock()) {
				try {
					List<HmBannerVO> result = getAllEnabledHmBanner();
					RedisUtils.set(CacheKeyPrefix.CACHE_BANNER, result);
					return RUtils.success("Banner列表", result);
				} finally {
					lock.unlock();
				}
			} else {
				return RUtils.success("Banner列表", new ArrayList<>());
			}
		} else {
			return RUtils.success("Banner列表", bannerList);
		}
	}

	/**
	 * 刷新 Banner 缓存
	 */
	public R refresh() {
		List<HmBannerVO> result = getAllEnabledHmBanner();
		boolean b = RedisUtils.set(CacheKeyPrefix.CACHE_BANNER, result);
		return RUtils.commonFailOrNot(b ? 1 : 0, "Banner缓存刷新");
	}

	/**
	 * 获取所有可用的Banner
	 */
	public List<HmBannerVO> getAllEnabledHmBanner() {
		List<HmBannerEntity> entityList = hmBannerMapper.selectList(
				Wrappers.lambdaQuery(HmBannerEntity.class)
						.eq(HmBannerEntity::getEnable, true)
		);
		return entityList.stream()
				.parallel()
				.map(e -> (HmBannerVO) new HmBannerVO().convertFrom(e))
				.collect(Collectors.toList());
	}

	/**
	 * 分页搜索
	 */
	public R list(HmBannerSearchParam param) {
		String title = param.getTitle();
		Boolean enable = param.getEnable();
		// 条件构造
		Page<HmBannerEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		Wrapper<HmBannerEntity> wrapper = Wrappers.lambdaQuery(HmBannerEntity.class)
				.eq(Objects.nonNull(enable), HmBannerEntity::getEnable, enable)
				.like(StringUtils.hasText(title), HmBannerEntity::getTitle, title)
				.orderByAsc(HmBannerEntity::getSort);
		// 分页查询
		Page<HmBannerEntity> entityPage = hmBannerMapper.selectPage(page, wrapper);
		// 数据转换
		PageResult<HmBannerVO> voPageResult = covertToPageResult(entityPage);
		// 返回
		return RUtils.success("首页Banner列表信息", voPageResult);
	}

	/**
	 * 创建 Banner
	 */
	public R create(HmBannerVO vo, MultipartFile file) throws IOException {
		if (Objects.nonNull(file)) {
			R r = updateAvatar(null, file);
			if (r.getStatus() != 200) {
				return r;
			}
			vo.setImageUrl((String) r.getData());
		}
		// 转换数据
		HmBannerEntity entity = vo.convertTo(new HmBannerEntity());
		// 执行插入
		int i = hmBannerMapper.insert(entity);
		return RUtils.commonFailOrNot(i, "创建Banner");
	}

	/**
	 * 更新 Banner
	 */
	public R update(HmBannerVO vo, MultipartFile file) throws IOException {
		String imageUrl = null;
		// 判断是否更新图片
		if (Objects.nonNull(file)) {
			R r = updateAvatar(vo.getId(), file);
			if (r.getStatus() != 200) {
				return r;
			}
			imageUrl = (String) r.getData();
		}
		String title = vo.getTitle();
		String linkUrl = vo.getLinkUrl();
		Integer sort = vo.getSort();
		Boolean enable = vo.getEnable();
		// 更新
		int i = hmBannerMapper.update(null,
				Wrappers.lambdaUpdate(HmBannerEntity.class)
						.eq(HmBannerEntity::getId, vo.getId())
						.set(StringUtils.hasText(title), HmBannerEntity::getTitle, title)
						.set(StringUtils.hasText(linkUrl), HmBannerEntity::getLinkUrl, linkUrl)
						.set(StringUtils.hasText(imageUrl), HmBannerEntity::getImageUrl, imageUrl)
						.set(Objects.nonNull(sort), HmBannerEntity::getSort, sort)
						.set(Objects.nonNull(enable), HmBannerEntity::getEnable, enable)
		);
		return RUtils.commonFailOrNot(i, "更新Banner");
	}

	/**
	 * 禁用Banner
	 */
	public R disable(int bannerId) {
		int i = hmBannerMapper.update(null,
				Wrappers.lambdaUpdate(HmBannerEntity.class)
						.eq(HmBannerEntity::getId, bannerId)
						.set(HmBannerEntity::getEnable, false)
		);
		return RUtils.commonFailOrNot(i, "Banner禁用");
	}

	/**
	 * 启用Banner
	 */
	public R enable(int bannerId) {
		int i = hmBannerMapper.update(null,
				Wrappers.lambdaUpdate(HmBannerEntity.class)
						.eq(HmBannerEntity::getId, bannerId)
						.set(HmBannerEntity::getEnable, true)
		);
		return RUtils.commonFailOrNot(i, "Banner启用");
	}

	/**
	 * 删除Banner
	 */
	public R delete(int bannerId) {
		HmBannerEntity entity = hmBannerMapper.selectById(bannerId);
		// 删除图片云资源
		if (Objects.nonNull(entity)) {
			aliyunOssService.delete(entity.getImageUrl());
		}
		int i = hmBannerMapper.deleteById(bannerId);
		return RUtils.commonFailOrNot(i, "删除Banner");
	}

	/**
	 * 返回图片url（针对管理员更新）
	 * 如果userId不为空且大于0，则删除目标用户头像,
	 */
	public R updateAvatar(Integer userId, MultipartFile file) throws IOException {
		// 图片大小限制在 2MB
		if (file.getSize() > 2097152L) {
			return RUtils.fail("图片文件不能超过2MB");
		}
		String type = FileTypeUtil.getType(file.getInputStream());
		// 判断是否目标格式图片文件
		if ("jpg".equals(type) || "jpeg".equals(type) || "png".equals(type)) {
			String newAvatarUrl = aliyunOssService.upload(file);
			// 头像上传成功
			if (StringUtils.hasText(newAvatarUrl)) {
				if (Objects.nonNull(userId) && userId > 0) {
					HmBannerEntity entity = hmBannerMapper.selectById(userId);
					if (Objects.isNull(entity)) {
						return RUtils.fail("Banner不存在");
					}
					// 删除原有图片
					aliyunOssService.delete(entity.getImageUrl());
				}
				return RUtils.success("新图片URL", newAvatarUrl);
			}
		} else {
			return RUtils.fail("图片格式不支持");
		}
		return RUtils.fail("Banner更新出错");
	}

	/**
	 * 转换成分页数据
	 */
	public PageResult<HmBannerVO> covertToPageResult(IPage<HmBannerEntity> entityIPage) {
		List<HmBannerVO> voList = entityIPage.getRecords().stream()
				.parallel()
				.map(e -> (HmBannerVO) new HmBannerVO().convertFrom(e))
				.collect(Collectors.toList());
		return new PageResult<>(entityIPage.getTotal(), voList);
	}

}
