package com.show.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.show.domain.VideoOpen;
import com.show.pojo.Videos;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author 916202420@qq.com
 * @date 2022/5/16 21:20
 */
@Mapper
public interface MoVideosMapper extends BaseMapper<Videos> {
}