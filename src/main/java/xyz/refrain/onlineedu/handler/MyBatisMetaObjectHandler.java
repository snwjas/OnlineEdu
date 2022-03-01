package xyz.refrain.onlineedu.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis 自动填充策略
 *
 * @author Myles Yang
 */
@Component
public class MyBatisMetaObjectHandler implements MetaObjectHandler {

	@Override
	public void insertFill(MetaObject metaObject) {
		this.fillStrategy(metaObject, "createTime", LocalDateTime.now());
		this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());
	}

}
