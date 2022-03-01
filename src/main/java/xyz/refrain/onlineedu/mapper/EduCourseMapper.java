package xyz.refrain.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.refrain.onlineedu.model.entity.EduCourseEntity;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author Myles Yang
 * @since 2021-01-16
 */
public interface EduCourseMapper extends BaseMapper<EduCourseEntity> {
	/**
	 * 字段增减
	 *
	 * @param id    主键id
	 * @param col   列名
	 * @param delta 增/减幅
	 * @return
	 */
	int colInc(@Param("id") int id, @Param("col") String col, @Param("delta") int delta);
}
