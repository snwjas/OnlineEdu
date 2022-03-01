package xyz.refrain.onlineedu.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Spring Validation 配置
 *
 * @author Myles Yang
 */
@Configuration
public class HibernateValidationConfig {

	/**
	 * 开启Fail Fast模式，一旦校验失败就立即返回
	 * Spring Validation 默认会校验完所有字段，然后才抛出异常。
	 */
	@Bean
	public Validator validator() {
		ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
				.configure()
				.failFast(true)
				.buildValidatorFactory();
		return validatorFactory.getValidator();
	}

	/**
	 * 方法参数检验，注意在类上添加 @Validated  注解
	 */
	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
		// 设置validator模式为快速失败返回
		postProcessor.setValidator(validator());
		return postProcessor;
	}

}
