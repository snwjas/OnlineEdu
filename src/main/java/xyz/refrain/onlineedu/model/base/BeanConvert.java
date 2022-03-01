package xyz.refrain.onlineedu.model.base;


import org.springframework.beans.BeanUtils;
import org.springframework.lang.NonNull;

/**
 * po dto vo ... bean转换
 *
 * @author Myles Yang
 */
public interface BeanConvert {

	@SuppressWarnings("unchecked")
	default <T extends BeanConvert> T convertFrom(@NonNull Object source, String... ignoreProperties) {
		if (source == null) {
			return null;
		}
		BeanUtils.copyProperties(source, this, ignoreProperties);
		return (T) this;
	}

	default <T> T convertTo(@NonNull T target, String... ignoreProperties) {
		if (target == null) {
			return null;
		}
		BeanUtils.copyProperties(this, target, ignoreProperties);
		return target;
	}
}
