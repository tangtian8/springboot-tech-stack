package top.tangtian.sharding.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import top.tangtian.sharding.domain.User;

/**
 * @author tangtian
 * @date 2025-10-20 09:15
 */
@Mapper
@Repository
public  interface UserMapper extends BaseMapper<User> {
}
