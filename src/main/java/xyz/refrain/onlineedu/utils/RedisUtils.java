package xyz.refrain.onlineedu.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 */
@Component
public class RedisUtils {

	private static RedisTemplate<String, Object> redisTemplate;

	@Autowired
	public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
		RedisUtils.redisTemplate = redisTemplate;
		// 测试 redis 是否连接正常
		if (RedisUtils.isClosed()) {
			throw new RedisConnectionFailureException("连接Redis服务失败");
		}
	}

	/**
	 * 检测Redis是否已关闭连接
	 *
	 * @return true / false
	 */
	public static boolean isClosed() {
		try {
			return Optional.ofNullable(redisTemplate.getConnectionFactory())
					.map(RedisConnectionFactory::getConnection)
					.map(RedisConnection::isClosed)
					.orElse(true);
		} catch (Exception ignored) {
		}
		return true;
	}

	/**
	 * 指定key失效时间
	 *
	 * @param key     键
	 * @param seconds 时间（秒）
	 * @return true / false
	 */
	public static boolean expire(String key, long seconds) {
		try {
			if (seconds > 0) {
				Boolean result = redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
				return result != null && result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 根据 key 获取过期时间（单位秒）
	 *
	 * @param key 键
	 * @return key的过期时间（单位秒），-3 异常，-2 key不存在，-1 key存在且无过期时间
	 */
	public static long getExpire(String key) {
		try {
			Long expireTime = redisTemplate.getExpire(key, TimeUnit.SECONDS);
			return expireTime == null ? -3L : expireTime;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -3L;
	}

	/**
	 * 判断 key 是否存在
	 *
	 * @param key 键
	 * @return true / false
	 */
	public static boolean hasKey(String key) {
		try {
			Boolean result = redisTemplate.hasKey(key);
			return result != null && result;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取键
	 *
	 * @param pattern
	 * @return
	 */
	public static Set<String> keys(String pattern) {
		return redisTemplate.keys(pattern);
	}

	/**
	 * 删除给定的键
	 *
	 * @param keys 键不为null
	 * @return 删除的键的数量
	 */
	public static long del(String... keys) {
		if (keys != null && keys.length > 0) {
			Long numberOfDeletedKeys = redisTemplate.delete(Arrays.asList(keys));
			return numberOfDeletedKeys == null ? 0L : numberOfDeletedKeys;
		}
		return 0L;
	}

//    ============================== String ==============================

	/**
	 * 普通缓存获取
	 *
	 * @param key 键
	 * @return 值
	 */
	public static Object get(String key) {
		return key == null ? null : redisTemplate.opsForValue().get(key);
	}

	/**
	 * 普通缓存放入
	 *
	 * @param key   键
	 * @param value 值
	 * @return true / false
	 */
	public static boolean set(String key, Object value) {
		try {
			redisTemplate.opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 普通缓存放入并设置时间
	 *
	 * @param key     键
	 * @param value   值
	 * @param seconds 时间（秒），如果 time < 0 则设置无限时间
	 * @return true / false
	 */
	public static boolean set(String key, Object value, long seconds) {
		try {
			if (seconds > 0) {
				redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
			} else {
				set(key, value);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 递增
	 *
	 * @param key   键
	 * @param delta 递增大小
	 * @return
	 */
	public static long incr(String key, long delta) {
		if (delta < 0) {
			throw new RuntimeException("递增因子必须大于 0");
		}
		Long res = redisTemplate.opsForValue().increment(key, delta);
		return res == null ? -1L : res;
	}

	/**
	 * 递减
	 *
	 * @param key   键
	 * @param delta 递减大小
	 * @return
	 */
	public static long decr(String key, long delta) {
		if (delta < 0) {
			throw new RuntimeException("递减因子必须大于 0");
		}
		return redisTemplate.opsForValue().increment(key, delta);
	}

//    ============================== Map ==============================

	/**
	 * HashGet
	 *
	 * @param key  键（no null）
	 * @param item 项（no null）
	 * @return 值
	 */
	public static Object hget(String key, String item) {
		return redisTemplate.opsForHash().get(key, item);
	}

	/**
	 * 获取 key 对应的 map
	 *
	 * @param key 键（no null）
	 * @return 对应的多个键值
	 */
	public static Map<Object, Object> hmget(String key) {
		return redisTemplate.opsForHash().entries(key);
	}

	/**
	 * HashSet
	 *
	 * @param key 键
	 * @param map 值
	 * @return true / false
	 */
	public static boolean hmset(String key, Map<Object, Object> map) {
		try {
			redisTemplate.opsForHash().putAll(key, map);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * HashSet 并设置时间
	 *
	 * @param key  键
	 * @param map  值
	 * @param time 时间
	 * @return true / false
	 */
	public static boolean hmset(String key, Map<Object, Object> map, long time) {
		try {
			redisTemplate.opsForHash().putAll(key, map);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 向一张 Hash表 中放入数据，如不存在则创建
	 *
	 * @param key   键
	 * @param item  项
	 * @param value 值
	 * @return true / false
	 */
	public static boolean hset(String key, String item, Object value) {
		try {
			redisTemplate.opsForHash().put(key, item, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 向一张 Hash表 中放入数据，并设置时间，如不存在则创建
	 *
	 * @param key   键
	 * @param item  项
	 * @param value 值
	 * @param time  时间（如果原来的 Hash表 设置了时间，这里会覆盖）
	 * @return true / false
	 */
	public static boolean hset(String key, String item, Object value, long time) {
		try {
			redisTemplate.opsForHash().put(key, item, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除 Hash表 中的值
	 *
	 * @param key  键
	 * @param item 项（可以多个，no null）
	 */
	public static void hdel(String key, Object... item) {
		redisTemplate.opsForHash().delete(key, item);
	}

	/**
	 * 判断 Hash表 中是否有该键的值
	 *
	 * @param key  键（no null）
	 * @param item 值（no null）
	 * @return true / false
	 */
	public static boolean hHasKey(String key, String item) {
		return redisTemplate.opsForHash().hasKey(key, item);
	}

	/**
	 * Hash递增，如果不存在则创建一个，并把新增的值返回
	 *
	 * @param key  键
	 * @param item 项
	 * @param by   递增大小 > 0
	 * @return
	 */
	public static Double hincr(String key, String item, Double by) {
		return redisTemplate.opsForHash().increment(key, item, by);
	}

	/**
	 * Hash递减
	 *
	 * @param key  键
	 * @param item 项
	 * @param by   递减大小
	 * @return
	 */
	public static Double hdecr(String key, String item, Double by) {
		return redisTemplate.opsForHash().increment(key, item, -by);
	}

//    ============================== Set ==============================

	/**
	 * 根据 key 获取 set 中的所有值
	 *
	 * @param key 键
	 * @return 值
	 */
	public static Set<Object> sGet(String key) {
		try {
			return redisTemplate.opsForSet().members(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 从键为 key 的 set 中，根据 value 查询是否存在
	 *
	 * @param key   键
	 * @param value 值
	 * @return true / false
	 */
	public static boolean sHasKey(String key, Object value) {
		try {
			return redisTemplate.opsForSet().isMember(key, value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将数据放入 set缓存
	 *
	 * @param key    键值
	 * @param values 值（可以多个）
	 * @return 成功个数
	 */
	public static long sSet(String key, Object... values) {
		try {
			return redisTemplate.opsForSet().add(key, values);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 将数据放入 set缓存，并设置时间
	 *
	 * @param key    键
	 * @param time   时间
	 * @param values 值（可以多个）
	 * @return 成功放入个数
	 */
	public static long sSet(String key, long time, Object... values) {
		try {
			long count = redisTemplate.opsForSet().add(key, values);
			if (time > 0) {
				expire(key, time);
			}
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 获取 set缓存的长度
	 *
	 * @param key 键
	 * @return 长度
	 */
	public static long sGetSetSize(String key) {
		try {
			return redisTemplate.opsForSet().size(key);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 移除 set缓存中，值为 value 的
	 *
	 * @param key    键
	 * @param values 值
	 * @return 成功移除个数
	 */
	public static long setRemove(String key, Object... values) {
		try {
			return redisTemplate.opsForSet().remove(key, values);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

//    ============================== List ==============================

	/**
	 * 获取 list缓存的内容
	 *
	 * @param key   键
	 * @param start 开始
	 * @param end   结束（0 到 -1 代表所有值）
	 * @return
	 */
	public static List<Object> lGet(String key, long start, long end) {
		try {
			return redisTemplate.opsForList().range(key, start, end);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取 list缓存的长度
	 *
	 * @param key 键
	 * @return 长度
	 */
	public static long lGetListSize(String key) {
		try {
			return redisTemplate.opsForList().size(key);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 根据索引 index 获取键为 key 的 list 中的元素
	 *
	 * @param key   键
	 * @param index 索引
	 *              当 index >= 0 时 {0:表头, 1:第二个元素}
	 *              当 index < 0 时 {-1:表尾, -2:倒数第二个元素}
	 * @return 值
	 */
	public static Object lGetIndex(String key, long index) {
		try {
			return redisTemplate.opsForList().index(key, index);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将值 value 插入键为 key 的 list 中，如果 list 不存在则创建空 list
	 *
	 * @param key   键
	 * @param value 值
	 * @return true / false
	 */
	public static boolean lSet(String key, Object value) {
		try {
			redisTemplate.opsForList().rightPush(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将值 value 插入键为 key 的 list 中，并设置时间
	 *
	 * @param key   键
	 * @param value 值
	 * @param time  时间
	 * @return true / false
	 */
	public static boolean lSet(String key, Object value, long time) {
		try {
			redisTemplate.opsForList().rightPush(key, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将 values 插入键为 key 的 list 中
	 *
	 * @param key    键
	 * @param values 值
	 * @return true / false
	 */
	public static boolean lSetList(String key, List<Object> values) {
		try {
			redisTemplate.opsForList().rightPushAll(key, values);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将 values 插入键为 key 的 list 中，并设置时间
	 *
	 * @param key    键
	 * @param values 值
	 * @param time   时间
	 * @return true / false
	 */
	public static boolean lSetList(String key, List<Object> values, long time) {
		try {
			redisTemplate.opsForList().rightPushAll(key, values);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根据索引 index 修改键为 key 的值
	 *
	 * @param key   键
	 * @param index 索引
	 * @param value 值
	 * @return true / false
	 */
	public static boolean lUpdateIndex(String key, long index, Object value) {
		try {
			redisTemplate.opsForList().set(key, index, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 在键为 key 的 list 中删除值为 value 的元素
	 *
	 * @param key   键
	 * @param count 如果 count == 0 则删除 list 中所有值为 value 的元素
	 *              如果 count > 0 则删除 list 中最左边那个值为 value 的元素
	 *              如果 count < 0 则删除 list 中最右边那个值为 value 的元素
	 * @param value
	 * @return
	 */
	public static long lRemove(String key, long count, Object value) {
		try {
			return redisTemplate.opsForList().remove(key, count, value);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

}
