package com.show.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author 916202420@qq.com
 * @date 2022/5/17 16:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("video_open")
public class VideoOpen {
    @TableId
    private String id;
    @TableField("name")
    private String name;
    @TableField("description")
    private String description;
    @TableField("token")
    private String token;
    @TableField("auth")
    private String auth;
    @TableField("info")
    private String info;
    @TableField("ico_url")
    private String icoUrl;
}