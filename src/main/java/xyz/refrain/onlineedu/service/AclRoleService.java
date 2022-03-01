package xyz.refrain.onlineedu.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.refrain.onlineedu.mapper.AclRoleMapper;
import xyz.refrain.onlineedu.model.entity.AclRoleEntity;
import xyz.refrain.onlineedu.model.vo.R;
import xyz.refrain.onlineedu.model.vo.admin.AclRoleVO;
import xyz.refrain.onlineedu.utils.RUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AclRole Service
 *
 * @author Myles Yang
 */
@Service
@Slf4j
public class AclRoleService {

	@Resource
	private AclRoleMapper aclRoleMapper;

	/**
	 * 根据条件获取所有角色
	 *
	 * @param enable 是否禁用
	 */
	public R listAll(Boolean enable) {
		List<AclRoleEntity> aclRoleEntityList = aclRoleMapper.selectList(
				Wrappers.lambdaQuery(AclRoleEntity.class)
						.eq(Objects.nonNull(enable), AclRoleEntity::getEnable, enable)
		);
		List<AclRoleVO> aclRoleVOList = covertToListVO(aclRoleEntityList);
		return RUtils.success("角色列表", aclRoleVOList);
	}


	public List<AclRoleVO> covertToListVO(List<AclRoleEntity> aclRoleEntityList) {
		return aclRoleEntityList.stream()
				.parallel()
				.map(e -> (AclRoleVO) new AclRoleVO().convertFrom(e))
				.collect(Collectors.toList());
	}

}
