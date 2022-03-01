package xyz.refrain.onlineedu.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * 方法执行计时注解切面
 */
@Component
@Aspect
@Slf4j
@Order(1)
public class TimeCostAspect {

	private final ThreadLocal<Instant> instantThreadLocal = new ThreadLocal<>();


	@Pointcut("@annotation(xyz.refrain.onlineedu.annotation.TimeCost)")
	public void pointCut() {
	}

	@Before("pointCut()")
	public void before() {
		instantThreadLocal.set(Instant.now());
	}

	@AfterReturning("pointCut()")
	public void afterReturning(JoinPoint point) {
		if (Objects.nonNull(instantThreadLocal.get())) {
			Instant now = Instant.now();
			MethodSignature signature = (MethodSignature) point.getSignature();
			Method method = signature.getMethod();
			// String methodParameterTypes = StringUtils.join(method.getGenericParameterTypes(), ", ");
			log.info("类：{}，方法：{}，执行耗时：{}毫秒",
					method.getDeclaringClass().getName(),
					method.getName() /*+ "(" + methodParameterTypes + ")"*/,
					ChronoUnit.MILLIS.between(instantThreadLocal.get(), now));
			instantThreadLocal.remove();
		}
	}
}
