package org.live.user.provider.dao.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.live.user.provider.dao.po.UserPO;

@Mapper
public interface UserMapper extends BaseMapper<UserPO> {

}
