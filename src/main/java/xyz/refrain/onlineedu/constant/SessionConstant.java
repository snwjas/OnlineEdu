package xyz.refrain.onlineedu.constant;

/**
 * Session Constant
 */
public interface SessionConstant {

	// session 过期时间，秒
	long EXPIRE = 3600 * 72;

	// 存储用户信息的键的前缀
	String REDIS_NAMESPACE = "session:";

	// 后台管理人员，存储用户信息的键的前缀
	String REDIS_NAMESPACE_ACL_USER = REDIS_NAMESPACE + "acluser:";

	// 讲师用户，存储用户信息的键的前缀
	String REDIS_NAMESPACE_TEACHER = REDIS_NAMESPACE + "teacher:";

	// 前台用户，存储用户信息的键的前缀
	String REDIS_NAMESPACE_MEMBER = REDIS_NAMESPACE + "member:";

	// http headers中token的字段名
	String TOKEN_KEY = "X-Token";

}
