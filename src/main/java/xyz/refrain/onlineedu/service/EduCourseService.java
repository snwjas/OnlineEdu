package xyz.refrain.onlineedu.service;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import xyz.refrain.onlineedu.mapper.*;
import xyz.refrain.onlineedu.model.base.BeanConvert;
import xyz.refrain.onlineedu.model.entity.*;
import xyz.refrain.onlineedu.model.enums.CourseStatusEnum;
import xyz.refrain.onlineedu.model.enums.MessageRoleEnum;
import xyz.refrain.onlineedu.model.params.EduCourseSearchParam;
import xyz.refrain.onlineedu.model.securtiy.AclUserDetail;
import xyz.refrain.onlineedu.model.vo.PageResult;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.admin.EduCourseDetailVO;
import xyz.refrain.onlineedu.model.vo.admin.EduCourseSimpleVO;
import xyz.refrain.onlineedu.utils.IPUtils;
import xyz.refrain.onlineedu.utils.LambdaTypeUtils;
import xyz.refrain.onlineedu.utils.RUtils;
import xyz.refrain.onlineedu.utils.SessionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 首页 课程 服务
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class EduCourseService {

	@Resource
	private EduCourseMapper eduCourseMapper;

	@Resource
	private EduChapterMapper eduChapterMapper;

	@Resource
	private EduVideoMapper eduVideoMapper;

	@Resource
	private EduTeacherMapper eduTeacherMapper;

	@Resource
	private RelCourseMemberMapper relCourseMemberMapper;

	@Autowired
	private EduSubjectService eduSubjectService;

	@Autowired
	private AliyunOssService aliyunOssService;

	@Autowired
	private AliyunVodService aliyunVodService;

	@Autowired
	private SysMessageService sysMessageService;

	@Autowired
	private EduCourseTmpService eduCourseTmpService;

	/**
	 * 获取详情信息
	 */
	public R getDetails(int id) {
		EduCourseEntity entity = eduCourseMapper.selectById(id);
		EduCourseDetailVO result = new EduCourseDetailVO().convertFrom(entity);
		if (Objects.nonNull(result)) {
			result.setSubjectParent(eduSubjectService.listAllParent(result.getSubjectId()));
		}
		return RUtils.success("课程详细", result);
	}

	/**
	 * 分页搜索（后端）
	 */
	public R list(EduCourseSearchParam param) {
		String title = param.getTitle();
		Integer teacherId = param.getTeacherId();
		Integer subjectId = param.getSubjectId();
		Boolean free = param.getFree();
		CourseStatusEnum status = param.getStatus();
		Boolean enable = param.getEnable();
		// 条件构造
		Page<EduCourseEntity> page = new Page<>(param.getCurrent(), param.getPageSize());

		Wrapper<EduCourseEntity> wrapper = Wrappers.lambdaQuery(EduCourseEntity.class)
				.select(tfi -> !tfi.getColumn().equals(LambdaTypeUtils.getColumnName(EduCourseEntity::getDescription)))
				.eq(Objects.nonNull(enable), EduCourseEntity::getEnable, enable)
				.eq(Objects.nonNull(teacherId), EduCourseEntity::getTeacherId, teacherId)
				.eq(Objects.nonNull(subjectId), EduCourseEntity::getSubjectId, subjectId)
				// 免费
				.eq(Objects.nonNull(free) && free, EduCourseEntity::getPrice, 0)
				// 收费
				.gt(Objects.nonNull(free) && !free, EduCourseEntity::getPrice, 0)
				// 非 CourseStatusEnum.AUDITING
				.eq(Objects.nonNull(status) && !CourseStatusEnum.AUDITING.equals(status),
						EduCourseEntity::getStatus, status)
				.and(CourseStatusEnum.AUDITING.equals(status),
						wrp -> wrp.eq(EduCourseEntity::getStatus, CourseStatusEnum.FIRST_AUDITING)
								.or()
								.eq(EduCourseEntity::getStatus, CourseStatusEnum.SECOND_AUDITING)
				)
				.like(StringUtils.hasText(title), EduCourseEntity::getTitle, title)
				.orderByAsc(EduCourseEntity::getSort);
		// 分页查询
		Page<EduCourseEntity> entityPage = eduCourseMapper.selectPage(page, wrapper);
		// 数据转换
		PageResult<EduCourseSimpleVO> voPageResult = covertToPageResult(entityPage, true);
		// 返回
		return RUtils.success("课程列表信息", voPageResult);
	}

	/**
	 * 分页搜索，前端
	 */
	public R listForApp(EduCourseSearchParam param) {
		String title = param.getTitle();
		Integer teacherId = param.getTeacherId();
		Integer subjectId = param.getSubjectId();
		// 条件构造
		Page<EduCourseEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		Wrapper<EduCourseEntity> wrapper = Wrappers.lambdaQuery(EduCourseEntity.class)
				.select(tfi -> !tfi.getColumn().equals(LambdaTypeUtils.getColumnName(EduCourseEntity::getDescription)))
				// 未下架的
				.eq(EduCourseEntity::getEnable, true)
				.eq(Objects.nonNull(teacherId), EduCourseEntity::getTeacherId, teacherId)
				.eq(Objects.nonNull(subjectId), EduCourseEntity::getSubjectId, subjectId)
				// 发表的状态和二次审核状态的
				.and(wrp -> wrp.eq(EduCourseEntity::getStatus, CourseStatusEnum.PUBLISH)
						.or()
						.eq(EduCourseEntity::getStatus, CourseStatusEnum.SECOND_AUDITING)
				)
				.like(StringUtils.hasText(title), EduCourseEntity::getTitle, title)
				.orderByAsc(EduCourseEntity::getSort);
		// 分页查询
		Page<EduCourseEntity> entityPage = eduCourseMapper.selectPage(page, wrapper);
		// 数据转换
		PageResult<EduCourseSimpleVO> voPageResult = covertToPageResult(entityPage, false);
		// 返回
		return RUtils.success("课程列表信息", voPageResult);
	}

	/**
	 * 创建课程（讲师创建）
	 */
	public R create(EduCourseDetailVO vo, MultipartFile file) throws IOException {
		if (Objects.nonNull(file)) {
			R r = updateAvatar(null, file);
			if (r.getStatus() != 200) {
				return r;
			}
			vo.setCover((String) r.getData());
		}
		// 转换数据
		EduCourseEntity entity = vo.convertTo(new EduCourseEntity());
		entity.setBuyCount(0)
				.setViewCount(0);
		// 执行插入
		eduCourseMapper.insert(entity);
		return RUtils.success("创建课程成功", entity);
	}

	/**
	 * 更新课程
	 */
	public R update(EduCourseDetailVO vo, MultipartFile file) throws IOException {
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
		Integer subjectId = vo.getSubjectId();
		Double price = vo.getPrice();
		String description = vo.getDescription();
		Integer sort = vo.getSort();
		// 更新
		int i = eduCourseMapper.update(null,
				Wrappers.lambdaUpdate(EduCourseEntity.class)
						.eq(EduCourseEntity::getId, vo.getId())
						.set(StringUtils.hasText(title), EduCourseEntity::getTitle, title)
						.set(StringUtils.hasText(imageUrl), EduCourseEntity::getCover, imageUrl)
						.set(StringUtils.hasText(description), EduCourseEntity::getDescription, description)
						.set(Objects.nonNull(subjectId), EduCourseEntity::getSubjectId, subjectId)
						.set(Objects.nonNull(price), EduCourseEntity::getPrice, price)
						.set(Objects.nonNull(sort), EduCourseEntity::getSort, sort)
		);
		return RUtils.commonFailOrNot(i, "更新课程");
	}

	/**
	 * 下架课程
	 */
	public R disable(int id) {
		int i = eduCourseMapper.update(null,
				Wrappers.lambdaUpdate(EduCourseEntity.class)
						.eq(EduCourseEntity::getId, id)
						.set(EduCourseEntity::getEnable, false)
		);
		return RUtils.commonFailOrNot(i, "课程下架");
	}

	/**
	 * 上架课程
	 */
	public R enable(int Id) {
		int i = eduCourseMapper.update(null,
				Wrappers.lambdaUpdate(EduCourseEntity.class)
						.eq(EduCourseEntity::getId, Id)
						.set(EduCourseEntity::getEnable, true)
		);
		return RUtils.commonFailOrNot(i, "课程启用");
	}

	/**
	 * 修改课程状态
	 */
	public boolean changeStatus(int Id, CourseStatusEnum status) {
		int i = eduCourseMapper.update(null,
				Wrappers.lambdaUpdate(EduCourseEntity.class)
						.eq(EduCourseEntity::getId, Id)
						.set(EduCourseEntity::getStatus, status)
		);
		return i > 0;
	}

	/**
	 * 检查课程是否是审核状态
	 *
	 * @param courseId 课程ID
	 * @return true 是
	 */
	public boolean checkCourseIsAuditing(int courseId) {
		EduCourseEntity entity = eduCourseMapper.selectOne(
				Wrappers.lambdaQuery(EduCourseEntity.class)
						.select(EduCourseEntity::getStatus)
						.eq(EduCourseEntity::getId, courseId)
		);
		if (Objects.isNull(entity)) {
			return false;
		}
		return CourseStatusEnum.AUDITING.equals(entity.getStatus())
				|| CourseStatusEnum.FIRST_AUDITING.equals(entity.getStatus())
				|| CourseStatusEnum.SECOND_AUDITING.equals(entity.getStatus());
	}

	/**
	 * 通过审核
	 */
	public R pass(Integer id) {
		// 判断课程审核状态，是初审还是二审
		EduCourseEntity entity = eduCourseMapper.selectOne(
				Wrappers.lambdaQuery(EduCourseEntity.class)
						.select(EduCourseEntity::getTeacherId, EduCourseEntity::getTitle, EduCourseEntity::getStatus)
						.eq(EduCourseEntity::getId, id)
		);
		int i = eduCourseMapper.update(null,
				Wrappers.lambdaUpdate(EduCourseEntity.class)
						.eq(EduCourseEntity::getId, id)
						.set(EduCourseEntity::getStatus, CourseStatusEnum.PUBLISH)
						.set(EduCourseEntity::getRemarks, "")
		);
		if (i > 0) {
			// 如果是二审，需要从临时表里移动数据回原表
			if (CourseStatusEnum.SECOND_AUDITING.equals(entity.getStatus())) {
				// 从临时表移动数据到原表
				eduCourseTmpService.moveChapterAndVideoToOriginalFromTemp(id);
				// 删除临时表数据
				eduCourseTmpService.deleteChapterAndVideoTemp(id, false);
			}
			// 更新总课时
			Integer count = eduVideoMapper.selectCount(
					Wrappers.lambdaQuery(EduVideoEntity.class)
							.eq(EduVideoEntity::getCourseId, id)
			);
			eduCourseMapper.update(null,
					Wrappers.lambdaUpdate(EduCourseEntity.class)
							.eq(EduCourseEntity::getId, id)
							.set(EduCourseEntity::getLessonNum, count)
			);
			// 发送消息
			AclUserDetail aclUser = SessionUtils.getAclUser(IPUtils.getRequest());
			sysMessageService.send(MessageRoleEnum.FROM_ADMIN, aclUser.getId(),
					MessageRoleEnum.TO_TEACHER, entity.getTeacherId(),
					StrUtil.format("恭喜！您的课程《{}》已通过审核上架！", entity.getTitle()));
		}

		return RUtils.commonFailOrNot(i, "通过课程审核");
	}

	/**
	 * 驳回审核
	 */
	public R turnDown(Integer id, String remarks) {
		int i = eduCourseMapper.update(null,
				Wrappers.lambdaUpdate(EduCourseEntity.class)
						.eq(EduCourseEntity::getId, id)
						.set(EduCourseEntity::getStatus, CourseStatusEnum.TURN_DOWN)
						.set(EduCourseEntity::getRemarks, remarks)
		);
		// 发送消息
		if (i > 0) {
			EduCourseEntity entity = eduCourseMapper.selectOne(
					Wrappers.lambdaQuery(EduCourseEntity.class)
							.select(EduCourseEntity::getTeacherId, EduCourseEntity::getTitle)
							.eq(EduCourseEntity::getId, id)
			);
			AclUserDetail aclUser = SessionUtils.getAclUser(IPUtils.getRequest());
			sysMessageService.send(MessageRoleEnum.FROM_ADMIN, aclUser.getId(),
					MessageRoleEnum.TO_TEACHER, entity.getTeacherId(),
					StrUtil.format("抱歉！您的课程《{}》未通过审核！详情请检查备注！", entity.getTitle()));
		}

		return RUtils.commonFailOrNot(i, "驳回课程审核");
	}

	/**
	 * 删除课程（会删除其下所有章节与视频）
	 */
	public R delete(int id) {
		EduCourseEntity entity = eduCourseMapper.selectById(id);
		if (Objects.isNull(entity)) {
			return RUtils.fail("课程不存在");
		}
		// 删除章节
		eduChapterMapper.delete(
				Wrappers.lambdaQuery(EduChapterEntity.class)
						.eq(EduChapterEntity::getCourseId, entity.getId())
		);
		// 删除视频与文件
		List<EduVideoEntity> videoEntityList = eduVideoMapper.selectList(
				Wrappers.lambdaQuery(EduVideoEntity.class)
						.select(EduVideoEntity::getId, EduVideoEntity::getVideoId)
						.eq(EduVideoEntity::getCourseId, entity.getId())
		);
		ArrayList<Integer> videoId = new ArrayList<>();
		ArrayList<String> videoSourceId = new ArrayList<>();
		videoEntityList.forEach(e -> {
			videoId.add(e.getId());
			videoSourceId.add(e.getVideoId());
		});
		if (!CollectionUtils.isEmpty(videoId)) {
			eduVideoMapper.deleteBatchIds(videoId);
		}
		if (!CollectionUtils.isEmpty(videoSourceId)) {
			aliyunVodService.deleteVideos(videoSourceId);
		}
		// 删除封面图片文件
		aliyunOssService.delete(entity.getCover());
		int i = eduCourseMapper.deleteById(id);
		return RUtils.commonFailOrNot(i, "删除课程");
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
					EduCourseEntity entity = eduCourseMapper.selectById(userId);
					if (Objects.isNull(entity)) {
						return RUtils.fail("课程不存在");
					}
					// 删除原有图片
					aliyunOssService.delete(entity.getCover());
				}
				return RUtils.success("新图片URL", newAvatarUrl);
			}
		} else {
			return RUtils.fail("图片格式不支持");
		}
		return RUtils.fail("课程更新出错");
	}

	/**
	 * 上传图片
	 */
	public R uploadPic(MultipartFile file) throws IOException {
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
				return RUtils.success("图片URL", newAvatarUrl);
			}
		} else {
			return RUtils.fail("图片格式不支持");
		}
		return RUtils.fail("图片上传出错");
	}

	/**
	 * 获取一条讲师未完成的课程（草稿箱）
	 */
	public R getCourseInDraft(int teacherId) {
		List<EduCourseEntity> entityList = eduCourseMapper.selectList(
				Wrappers.lambdaQuery(EduCourseEntity.class)
						.eq(EduCourseEntity::getTeacherId, teacherId)
						.eq(EduCourseEntity::getStatus, CourseStatusEnum.DRAFT)
		);
		if (CollectionUtils.isEmpty(entityList)) {
			return RUtils.success("该讲师没有在草稿箱的课程");
		}
		EduCourseEntity entity = entityList.get(0);
		BeanConvert result = new EduCourseDetailVO().convertFrom(entity);
		return RUtils.success("该讲师在草稿箱的课程", result);
	}

	/**
	 * 获取讲师所有的课程ID，和名称
	 */
	public List<Map<String, Object>> listTeacherCourseIdAndTitle(int teacherId) {
		List<EduCourseEntity> entityList = eduCourseMapper.selectList(
				Wrappers.lambdaQuery(EduCourseEntity.class)
						.select(EduCourseEntity::getId, EduCourseEntity::getTitle)
						.eq(EduCourseEntity::getTeacherId, teacherId)
		);
		return entityList.stream()
				.parallel()
				.map(e -> {
					HashMap<String, Object> map = new HashMap<>();
					map.put("id", e.getId());
					map.put("title", e.getTitle());
					return map;
				})
				.collect(Collectors.toList());
	}

	/**
	 * 判断学员是否订阅了某课程
	 */
	public R isBuyCourse(int memberId, int courseId) {
		Integer count = relCourseMemberMapper.selectCount(
				Wrappers.lambdaQuery(RelCourseMemberEntity.class)
						.eq(RelCourseMemberEntity::getMemberId, memberId)
						.eq(RelCourseMemberEntity::getCourseId, courseId)
		);
		return RUtils.success("学员是否已订阅课程", count > 0);
	}

	/**
	 * 获取讲师所有的课程ID
	 */
	public Set<Integer> getTeacherCourseIds(int teacherId) {
		List<EduCourseEntity> entityList = eduCourseMapper.selectList(
				Wrappers.lambdaQuery(EduCourseEntity.class)
						.select(EduCourseEntity::getId)
						.eq(EduCourseEntity::getTeacherId, teacherId)
		);
		return entityList.stream()
				.parallel()
				.map(EduCourseEntity::getId)
				.collect(Collectors.toSet());
	}

	/**
	 * 转换成分页数据
	 */
	public PageResult<EduCourseSimpleVO> covertToPageResult(IPage<EduCourseEntity> entityIPage,
	                                                        boolean getSubjectParent) {
		List<EduCourseSimpleVO> voList = entityIPage.getRecords().stream()
				.parallel()
				.map(e -> {
					EduCourseSimpleVO vo = new EduCourseSimpleVO().convertFrom(e);
					// 获取分类等级
					if (getSubjectParent && Objects.nonNull(vo.getSubjectId())) {
						vo.setSubjectParent(eduSubjectService.listAllParent(vo.getSubjectId()));
					}
					// 获取教师名字
					if (Objects.nonNull(vo.getTeacherId())) {
						EduTeacherEntity entity = eduTeacherMapper.selectOne(
								Wrappers.lambdaQuery(EduTeacherEntity.class)
										.select(EduTeacherEntity::getName)
										.eq(EduTeacherEntity::getId, vo.getTeacherId())
						);
						vo.setTeacherName(entity.getName());
					}
					return vo;
				})
				.collect(Collectors.toList());
		return new PageResult<>(entityIPage.getTotal(), voList);
	}

}
