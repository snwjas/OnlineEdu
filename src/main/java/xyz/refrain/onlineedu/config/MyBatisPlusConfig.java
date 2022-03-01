package xyz.refrain.onlineedu.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan("xyz.refrain.onlineedu.mapper")
public class MyBatisPlusConfig {

	/**
	 * MybatisPlus 拦截器
	 */
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

		// 注册分页插件
		PaginationInnerInterceptor pii = new PaginationInnerInterceptor(DbType.MYSQL);
		// 最大单页限制数量
		pii.setMaxLimit(100L);

		interceptor.addInnerInterceptor(pii);
		return interceptor;
	}

}
