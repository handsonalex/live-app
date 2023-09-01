package org.live.id.generate.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.live.id.generate.dao.po.IdGeneratePO;

import java.util.List;

@Mapper
public interface IdGenerateMapper extends BaseMapper<IdGeneratePO> {

    @Select("select * from t_id_generate_config")
    List<IdGeneratePO> selectAll();

    @Update("update t_id_generate_config set current_start = next_threshold," +
            "next_threshold=current_start+step,version=version+1 where id=#{id}")
    int updateNewIdCountAndVersion(@Param("id") int id,@Param("version") int version);
}
