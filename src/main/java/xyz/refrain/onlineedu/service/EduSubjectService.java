package xyz.refrain.onlineedu.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import xyz.refrain.onlineedu.constant.CacheKeyPrefix;
import xyz.refrain.onlineedu.mapper.EduSubjectMapper;
import xyz.refrain.onlineedu.model.entity.EduSubjectEntity;
import xyz.refrain.onlineedu.model.params.EduSubjectSearchParam;
import xyz.refrain.onlineedu.model.vo.PageResult;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.admin.EduSubjectDetailVO;
import xyz.refrain.onlineedu.model.vo.admin.EduSubjectSimpleChildrenVO;
import xyz.refrain.onlineedu.model.vo.admin.EduSubjectSimpleParentVO;
import xyz.refrain.onlineedu.utils.RUtils;
import xyz.refrain.onlineedu.utils.RedisUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Edu Subject Service
 * 课程科目(分类)
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class EduSubjectService {

	@Resource
	private EduSubjectMapper eduSubjectMapper;

	/**
	 * 互斥锁
	 */
	private final Lock lock = new ReentrantLock();

	/**
	 * 获取课程分类列表数据（从缓存中获取，如果获取失败从数据库中取，然后刷新缓存）
	 */
	public R get() {
		Object subjectList = RedisUtils.get(CacheKeyPrefix.CACHE_SUBJECT);
		if (Objects.isNull(subjectList)) {
			// 缓存中没有数据，去数据库取并设置到缓存去
			// 使用互斥锁，只能一个线程取，其他的返回空值
			if (lock.tryLock()) {
				try {
					// 获取分类
					List<EduSubjectSimpleChildrenVO> result = getAllEnabledFirstEduSubject();
					RedisUtils.set(CacheKeyPrefix.CACHE_SUBJECT, result);
					return RUtils.success("课程分类列表", result);
				} finally {
					lock.unlock();
				}
			} else {
				return RUtils.success("课程分类列表", new ArrayList<>());
			}
		} else {
			return RUtils.success("课程分类列表", subjectList);
		}
	}

	/**
	 * 获取所有可用的一级分类
	 */
	public List<EduSubjectSimpleChildrenVO> getAllEnabledFirstEduSubject() {
		// 获取分类
		List<EduSubjectEntity> entityList = eduSubjectMapper.selectList(
				Wrappers.lambdaQuery(EduSubjectEntity.class)
						.select(EduSubjectEntity::getId, EduSubjectEntity::getTitle)
						.eq(EduSubjectEntity::getEnable, true)
						.eq(EduSubjectEntity::getParentId, 0)
		);
		// 数据转换
		List<EduSubjectSimpleChildrenVO> voList = covertToSimpleListVO(entityList);
		// 递归查询所有子分类
		return listSimpleByRecursively(voList);
	}

	/**
	 * 刷新 Banner 缓存
	 */
	public R refresh() {
		List<EduSubjectSimpleChildrenVO> result = getAllEnabledFirstEduSubject();
		boolean b = RedisUtils.set(CacheKeyPrefix.CACHE_SUBJECT, result);
		return RUtils.commonFailOrNot(b ? 1 : 0, "课程缓存刷新");
	}

	/**
	 * 分页搜索
	 * 只搜索顶层分类（顶层分类父ID为0），嵌套分类不限层次
	 */
	public R list(EduSubjectSearchParam param) {
		Integer parentId = param.getParentId();
		String title = param.getTitle();
		Boolean enable = param.getEnable();
		// 条件构造
		Page<EduSubjectEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		Wrapper<EduSubjectEntity> wrapper = Wrappers.lambdaQuery(EduSubjectEntity.class)
				.eq(Objects.nonNull(parentId), EduSubjectEntity::getParentId, parentId)
				.eq(Objects.nonNull(enable), EduSubjectEntity::getEnable, enable)
				.like(StringUtils.hasText(title), EduSubjectEntity::getTitle, title)
				.orderByAsc(EduSubjectEntity::getSort);
		// 分页查询
		Page<EduSubjectEntity> entityPage = eduSubjectMapper.selectPage(page, wrapper);
		// 数据转换
		PageResult<EduSubjectDetailVO> voPageResult = covertToPageResult(entityPage);
		// 递归查询所有子分类
		List<EduSubjectDetailVO> voList = listDetailByRecursively(voPageResult.getList());
		voPageResult.setList(voList);
		// 返回
		return RUtils.success("课程分类列表信息", voPageResult);
	}

	/**
	 * 递归查询所有子分类
	 */
	List<EduSubjectDetailVO> listDetailByRecursively(List<EduSubjectDetailVO> voList) {
		// 递归出口
		if (CollectionUtils.isEmpty(voList)) {
			return null;
		}
		for (EduSubjectDetailVO vo : voList) {
			List<EduSubjectEntity> entityList = listAllChild(vo.getId());
			List<EduSubjectDetailVO> subVOList = covertToDetailListVO(entityList);
			vo.setChildren(listDetailByRecursively(subVOList));
		}
		return voList;
	}

	List<EduSubjectSimpleChildrenVO> listSimpleByRecursively(List<EduSubjectSimpleChildrenVO> voList) {
		// 递归出口
		if (CollectionUtils.isEmpty(voList)) {
			return null;
		}
		for (EduSubjectSimpleChildrenVO vo : voList) {
			List<EduSubjectEntity> entityList = listAllChild(vo.getId());
			List<EduSubjectSimpleChildrenVO> subVOList = covertToSimpleListVO(entityList);
			vo.setChildren(listSimpleByRecursively(subVOList));
		}
		return voList;
	}

	/**
	 * 列举所有子分类信息
	 */
	public List<EduSubjectEntity> listAllChild(int parentId) {
		return eduSubjectMapper.selectList(
				Wrappers.lambdaQuery(EduSubjectEntity.class)
						.eq(EduSubjectEntity::getParentId, parentId)
						.orderByAsc(EduSubjectEntity::getSort)
		);
	}

	/**
	 * 列取所有父分类
	 */
	public EduSubjectSimpleParentVO listAllParent(int childId) {
		EduSubjectSimpleParentVO curVO = new EduSubjectSimpleParentVO();
		EduSubjectSimpleParentVO resultVO = curVO;
		int parentId = childId;
		while (parentId != 0) {
			EduSubjectEntity entity = eduSubjectMapper.selectOne(
					Wrappers.lambdaQuery(EduSubjectEntity.class)
							.select(EduSubjectEntity::getId, EduSubjectEntity::getParentId, EduSubjectEntity::getTitle)
							.eq(EduSubjectEntity::getId, parentId)
			);
			if (Objects.isNull(entity)) {
				break;
			}
			curVO.setId(entity.getId())
					.setTitle(entity.getTitle());
			// 防止顶层查询两遍
			if (entity.getParentId() != 0) {
				curVO.setParent(new EduSubjectSimpleParentVO().convertFrom(entity));
			}
			// 下一次循环
			parentId = entity.getParentId();
			curVO = curVO.getParent();
		}
		return resultVO;
	}

	/**
	 * 创建分类
	 */
	public R create(EduSubjectDetailVO vo) {
		EduSubjectEntity entity = vo.convertTo(new EduSubjectEntity());
		int i = eduSubjectMapper.insert(entity);
		return RUtils.commonFailOrNot(i, "创建分类");
	}

	/**
	 * 更新分类
	 */
	public R update(EduSubjectDetailVO vo) {
		String title = vo.getTitle();
		Integer sort = vo.getSort();
		Boolean enable = vo.getEnable();
		int i = eduSubjectMapper.update(null,
				Wrappers.lambdaUpdate(EduSubjectEntity.class)
						.eq(EduSubjectEntity::getId, vo.getId())
						.set(StringUtils.hasText(title), EduSubjectEntity::getTitle, title)
						.set(Objects.nonNull(sort), EduSubjectEntity::getSort, sort)
						.set(Objects.nonNull(enable), EduSubjectEntity::getEnable, enable)
		);
		return RUtils.commonFailOrNot(i, "课程分类更新");
	}

	/**
	 * 禁用课程分类
	 */
	public R disable(int id) {
		int i = eduSubjectMapper.update(null,
				Wrappers.lambdaUpdate(EduSubjectEntity.class)
						.eq(EduSubjectEntity::getId, id)
						.set(EduSubjectEntity::getEnable, false)
		);
		// 递归禁用
		if (i > 0) {
			enOrDisAbleRecursively(id, false);
		}
		return RUtils.commonFailOrNot(i, "课程分类禁用");
	}

	/**
	 * 启用课程分类
	 */
	public R enable(int id) {
		int i = eduSubjectMapper.update(null,
				Wrappers.lambdaUpdate(EduSubjectEntity.class)
						.eq(EduSubjectEntity::getId, id)
						.set(EduSubjectEntity::getEnable, true)
		);
		// 递归启用
		if (i > 0) {
			enOrDisAbleRecursively(id, true);
		}
		return RUtils.commonFailOrNot(i, "课程分类启用");
	}

	/**
	 * 递归设置分类状态
	 */
	public void enOrDisAbleRecursively(int parentId, Boolean enable) {
		List<EduSubjectEntity> entityList = eduSubjectMapper.selectList(
				Wrappers.lambdaQuery(EduSubjectEntity.class)
						.select(EduSubjectEntity::getId)
						.eq(EduSubjectEntity::getParentId, parentId)
		);
		// 递归出口
		if (CollectionUtils.isEmpty(entityList)) {
			return;
		}
		// 设置分类状态
		for (EduSubjectEntity entity : entityList) {
			eduSubjectMapper.update(null,
					Wrappers.lambdaUpdate(EduSubjectEntity.class)
							.eq(EduSubjectEntity::getId, entity.getId())
							.set(EduSubjectEntity::getEnable, enable)
			);
			// 递归继续
			enOrDisAbleRecursively(entity.getId(), enable);
		}
	}

	/**
	 * 递归删除分类，删除一个分类，会删除他们的所有子分类
	 */
	public R delete(int id) {
		int i = eduSubjectMapper.deleteById(id);
		// 递归删除
		if (i > 0) {
			deleteByRecursively(id);
		}
		return RUtils.commonFailOrNot(i, "课程分类删除");
	}

	/**
	 * 递归删除分类
	 */
	public void deleteByRecursively(int parentId) {
		List<EduSubjectEntity> entityList = eduSubjectMapper.selectList(
				Wrappers.lambdaQuery(EduSubjectEntity.class)
						.select(EduSubjectEntity::getId)
						.eq(EduSubjectEntity::getParentId, parentId)
		);
		// 递归出口
		if (CollectionUtils.isEmpty(entityList)) {
			return;
		}
		List<Integer> ids = entityList.stream()
				.map(EduSubjectEntity::getId)
				.collect(Collectors.toList());
		// 删除
		eduSubjectMapper.deleteBatchIds(ids);
		// 递归继续
		ids.forEach(this::deleteByRecursively);
	}

	/**
	 * 转换成VO
	 */
	public List<EduSubjectDetailVO> covertToDetailListVO(List<EduSubjectEntity> entityList) {
		return entityList.stream()
				.parallel()
				.map(e -> (EduSubjectDetailVO) new EduSubjectDetailVO().convertFrom(e))
				.collect(Collectors.toList());
	}

	public List<EduSubjectSimpleChildrenVO> covertToSimpleListVO(List<EduSubjectEntity> entityList) {
		return entityList.stream()
				.parallel()
				.map(e -> (EduSubjectSimpleChildrenVO) new EduSubjectSimpleChildrenVO().convertFrom(e))
				.collect(Collectors.toList());
	}

	/**
	 * 转换成分页数据
	 */
	public PageResult<EduSubjectDetailVO> covertToPageResult(IPage<EduSubjectEntity> entityIPage) {
		List<EduSubjectDetailVO> voList = covertToDetailListVO(entityIPage.getRecords());
		return new PageResult<>(entityIPage.getTotal(), voList);
	}

}
