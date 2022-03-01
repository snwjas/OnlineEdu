package xyz.refrain.onlineedu.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.refrain.onlineedu.constant.CacheKeyPrefix;
import xyz.refrain.onlineedu.mapper.EduCourseMapper;
import xyz.refrain.onlineedu.mapper.RelCourseMemberMapper;
import xyz.refrain.onlineedu.mapper.TOrderMapper;
import xyz.refrain.onlineedu.model.entity.EduCourseEntity;
import xyz.refrain.onlineedu.model.entity.RelCourseMemberEntity;
import xyz.refrain.onlineedu.model.entity.TOrderEntity;
import xyz.refrain.onlineedu.model.enums.PayTypeEnum;
import xyz.refrain.onlineedu.model.params.TOrderSearchParam;
import xyz.refrain.onlineedu.model.vo.PageResult;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.admin.TOrderVO;
import xyz.refrain.onlineedu.model.vo.app.MemberOrderVO;
import xyz.refrain.onlineedu.utils.LambdaTypeUtils;
import xyz.refrain.onlineedu.utils.RUtils;
import xyz.refrain.onlineedu.utils.RedisUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单服务
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class TOrderService {

	@Resource
	private TOrderMapper tOrderMapper;

	@Resource
	private RelCourseMemberMapper relCourseMemberMapper;

	@Resource
	private EduCourseMapper eduCourseMapper;

	/**
	 * 分页查询
	 */
	public R list(TOrderSearchParam param) {
		Integer memberId = param.getMemberId();
		String orderNo = param.getOrderNo();
		PayTypeEnum payType = param.getPayType();
		LocalDateTime beginCreate = param.getBeginCreate();
		LocalDateTime endCreate = param.getEndCreate();
		// 条件构造
		Page<TOrderEntity> page = new Page<>(param.getCurrent(), param.getPageSize());
		Wrapper<TOrderEntity> wrapper = Wrappers.lambdaQuery(TOrderEntity.class)
				.eq(Objects.nonNull(memberId), TOrderEntity::getMemberId, memberId)
				.eq(Objects.nonNull(payType), TOrderEntity::getPayType, payType)
				.like(StringUtils.hasText(orderNo), TOrderEntity::getOrderNo, orderNo)
				.ge(Objects.nonNull(beginCreate), TOrderEntity::getCreateTime, beginCreate)
				.le(Objects.nonNull(endCreate), TOrderEntity::getCreateTime, endCreate);
		// 分页查询
		Page<TOrderEntity> entityPage = tOrderMapper.selectPage(page, wrapper);
		// 数据转换
		PageResult<TOrderVO> voPageResult = covertToPageResult(entityPage);
		// 返回
		return RUtils.success("订单列表信息", voPageResult);
	}

	/**
	 * 列举学员的订单
	 */
	public R listMemberOrders(TOrderSearchParam param) {
		Integer memberId = param.getMemberId();
		// 条件构造
		Page<TOrderEntity> page = new Page<>(param.getCurrent(), 10);
		Wrapper<TOrderEntity> wrapper = Wrappers.lambdaQuery(TOrderEntity.class)
				.eq(Objects.nonNull(memberId), TOrderEntity::getMemberId, memberId);
		// 分页查询
		Page<TOrderEntity> entityPage = tOrderMapper.selectPage(page, wrapper);
		// 数据转换
		List<MemberOrderVO> voList = entityPage.getRecords().stream()
				.parallel()
				.map(e -> {
					MemberOrderVO vo = new MemberOrderVO().convertFrom(e);
					EduCourseEntity entity = eduCourseMapper.selectOne(
							Wrappers.lambdaQuery(EduCourseEntity.class)
									.select(EduCourseEntity::getTitle)
									.eq(EduCourseEntity::getId, vo.getCourseId())
					);
					vo.setCourseName(Objects.isNull(entity) ? "" : entity.getTitle());
					return vo;
				})
				.collect(Collectors.toList());
		PageResult<MemberOrderVO> voPageResult = new PageResult<>(entityPage.getTotal(), voList);
		// 返回
		return RUtils.success("订单列表信息", voPageResult);
	}

	/**
	 * 创建订单,返回订单订单号
	 */
	public String create(TOrderVO vo) {
		// 生成订单号
		String key = CacheKeyPrefix.TORDER_INC + LocalDate.now();
		Integer inc_num = (Integer) RedisUtils.get(key);
		if (Objects.isNull(inc_num)) {
			inc_num = (int) RedisUtils.incr(key, 1);
			// 设置过期时间，一天
			RedisUtils.expire(key, 86400);
		}
		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String orderNo = date + inc_num;
		// 转换和设置数据
		TOrderEntity entity = vo.convertTo(new TOrderEntity());
		entity.setOrderNo(orderNo);
		entity.setPayType(PayTypeEnum.NONE);
		// 执行插入
		int i = tOrderMapper.insert(entity);
		return i > 0 ? orderNo : null;
	}

	/**
	 * 创建支付订单
	 */
	public R createOrder(TOrderVO vo) {
		// 检查以前是否支付过了或者未支付的订单
		TOrderEntity entity = tOrderMapper.selectOne(
				Wrappers.lambdaQuery(TOrderEntity.class)
						.eq(TOrderEntity::getMemberId, vo.getMemberId())
						.eq(TOrderEntity::getCourseId, vo.getCourseId())
		);
		// 已经支付过了
		if (Objects.nonNull(entity) && Objects.nonNull(entity.getPayType())
				&& !PayTypeEnum.NONE.equals(entity.getPayType())) {
			return RUtils.fail("该课程您已经订阅");
		}
		// 创建了订单，但是未支付，继续使用该订单，但是更新创建时间，支付金额
		if (Objects.nonNull(entity) && Objects.nonNull(entity.getPayType())
				&& PayTypeEnum.NONE.equals(entity.getPayType())) {
			entity.setCreateTime(LocalDateTime.now());
			entity.setTotalFee(vo.getTotalFee());
			return RUtils.success("支付订单创建成功", entity.getOrderNo());
		}
		// 第一次创建订单
		vo.setPayType(PayTypeEnum.NONE);
		String orderNo = create(vo);
		if (!StringUtils.hasText(orderNo)) {
			return RUtils.fail("支付订单创建失败，请重试");
		}
		return RUtils.success("支付订单创建成功", orderNo);
	}

	/**
	 * 订单支付成功
	 */
	public R paySucceed(String orderNo) {
		TOrderEntity entity = tOrderMapper.selectOne(
				Wrappers.lambdaQuery(TOrderEntity.class)
						.eq(TOrderEntity::getOrderNo, orderNo)
		);
		if (Objects.isNull(entity)) {
			return RUtils.fail("订单不存在");
		}
		// 设置订单订单支付类型和流水号
		PayTypeEnum payType = entity.getId() % 2 == 0 ? PayTypeEnum.ALI_PAY : PayTypeEnum.WECHAT_PAY;
		String uuid = org.apache.commons.lang3.StringUtils.replace(UUID.randomUUID().toString(), "-", "");
		String transactionNum = orderNo + uuid.substring(0, 13);
		set(entity.getId(), payType, transactionNum);
		// 添加至课程学员关联表
		RelCourseMemberEntity relCourseMemberEntity = new RelCourseMemberEntity()
				.setCourseId(entity.getCourseId())
				.setMemberId(entity.getMemberId());
		relCourseMemberMapper.insert(relCourseMemberEntity);
		// 课程购买量+1
		String columnName = LambdaTypeUtils.getColumnName(EduCourseEntity::getBuyCount);
		eduCourseMapper.colInc(entity.getCourseId(), columnName, 1);
		return RUtils.success("课程订阅成功");
	}

	/**
	 * 支付成功设置订单支付类型和流水号
	 */
	public int set(int orderId, PayTypeEnum payType, String transactionNum) {
		return tOrderMapper.update(null,
				Wrappers.lambdaUpdate(TOrderEntity.class)
						.eq(TOrderEntity::getId, orderId)
						.set(TOrderEntity::getPayTime, LocalDateTime.now())
						.set(Objects.nonNull(payType), TOrderEntity::getPayType, payType)
						.set(StringUtils.hasText(transactionNum), TOrderEntity::getTransactionNum, transactionNum)
		);
	}

	/**
	 * 转换成分页数据
	 */
	public PageResult<TOrderVO> covertToPageResult(IPage<TOrderEntity> entityIPage) {
		List<TOrderVO> voList = entityIPage.getRecords().stream()
				.parallel()
				.map(e -> (TOrderVO) new TOrderVO().convertFrom(e))
				.collect(Collectors.toList());
		return new PageResult<>(entityIPage.getTotal(), voList);
	}

}
