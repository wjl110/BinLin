package com.show.mapper;

import java.util.List;
import java.util.Set;

import com.show.pojo.Videos;
import com.show.pojo.VideosVo;
import com.show.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

public interface VideosMapper extends MyMapper<Videos> {

    List<Videos> selectByIds(@Param("set") Set<String> ids);
}