package xyz.refrain.onlineedu.constant;

/**
 * Redis key 前缀 常量
 *
 * @author Myles Yang
 */
public interface CacheKeyPrefix {

	/**
	 * 分隔符
	 */
	String SEPARATOR = ":";

	/**
	 * 访问限制次数（接口限流）
	 */
	String ACCESS_LIMIT_PREFIX = "accessLimit:";

	/**
	 * 首页 Banner 数据
	 */
	String CACHE_BANNER = "cache:banner";

	/**
	 * 课程分类数据
	 */
	String CACHE_SUBJECT = "cache:subject";

	/**
	 * 订单号递增序号
	 */
	String TORDER_INC = "torder:inc:";


}
