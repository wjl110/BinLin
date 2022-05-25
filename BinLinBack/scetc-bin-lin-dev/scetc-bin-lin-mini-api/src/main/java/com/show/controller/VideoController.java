package com.show.controller;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.show.other.AppConst;
import com.show.other.RedisUtils;
import com.show.other.VideoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.show.pojo.Bgm;
import com.show.pojo.PageResult;
import com.show.pojo.VideoStatusEnum;
import com.show.pojo.Videos;
import com.show.pojo.VideosVo;
import com.show.service.BgmService;
import com.show.service.VideoService;
import com.show.utils.FetchVideo;
import com.show.utils.MergeVideo;
import com.show.utils.XyfJsonResult;
import com.show.vo.CommentsVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import static com.show.other.AppConst.ElasticSearch.bin_lin_video;
import static com.show.other.AppConst.Redis.VIDEO_ID_SET;

/**
 * @author 916202420@qq.com 创建时间 2022年5月15日 中午12:00:00
 */
@RestController
@Api(tags = {"视频接口"})
@RequestMapping("/video")
@Slf4j
public class VideoController extends BasicController {

    @Autowired
    private BgmService bgmService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private VideoService videoService;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Async
    public void parseFile(Videos videos) {
        log.debug("开始上传->{}", videos);
        AppConst.executorService.execute(() -> {
            try {
                String videoPath = videos.getVideoPath();
                String videoAbsolutePath = FILe_SPACE + videoPath;
                if (!new File(videoAbsolutePath).isFile()) {
                    return;
                }
                String wavPath = videoPath.substring(0, videoPath.lastIndexOf('.') + 1) + "wav";
                String wavAbsolutePath = FILe_SPACE + wavPath;
                VideoUtils.getWavByVideo(videoAbsolutePath, wavAbsolutePath);
                String aLiYunVideoPath = "binlin/user" + wavPath;
                for (int i = 0; i < 20; i++) {
                    File wavFile = new File(wavAbsolutePath);
                    if (wavFile.isFile()) {
                        VideoUtils.uploadToALiYun(aLiYunVideoPath, new FileInputStream(wavFile));
                        log.debug("音频文件已上传->" + aLiYunVideoPath);
                        JSONArray sentences = VideoUtils.getSentencesByWav(VideoUtils.A_LI_YUN_OSS_PATH + "/" + aLiYunVideoPath);
                        if (sentences != null) {
                            sentences.forEach(o -> {
                                AppConst.executorService.execute(() -> {
                                    if (o instanceof JSONObject) {
                                        JSONObject jsonObject = (JSONObject) o;
                                        Integer endTime = jsonObject.getInteger("EndTime");
                                        Integer beginTime = jsonObject.getInteger("BeginTime");
                                        String imgPath = videoPath.substring(0, videoPath.lastIndexOf('.')) + beginTime + ".jpg";
                                        String imgAbsolutePath = FILe_SPACE + imgPath;
                                        String aLiYunImgPath = "binlin/user" + imgPath;
                                        String text = jsonObject.getString("Text");
                                        try {
                                            VideoUtils.getVideoJpgByTime((endTime + beginTime) / 2, videoAbsolutePath, imgAbsolutePath);
                                            for (int j = 0; j < 20; j++) {
                                                File imgFile = new File(imgAbsolutePath);
                                                if (imgFile.isFile()) {
                                                    VideoUtils.uploadToALiYun(aLiYunImgPath, new FileInputStream(imgFile));
                                                    log.debug("JPG文件已上传->" + aLiYunImgPath);
                                                    restHighLevelClient.index(new IndexRequest().index(bin_lin_video.name())
                                                            .id(videos.getId() + beginTime).source(
                                                                    new HashMap<String, Object>(10) {
                                                                        {
                                                                            put("beginTime", beginTime);
                                                                            put("endTime", endTime);
                                                                            put("text", text);
                                                                            put("videoDesc", videos.getVideoDesc());
                                                                            put("videoId", videos.getId());
                                                                            put("videoPage", videos.getVideoPath());
                                                                            put("videoCategory", videos.getVideoCategory());
                                                                            put("audioId", videos.getAudioId());
                                                                            put("userId", videos.getUserId());
                                                                            put("gitURL", VideoUtils.A_LI_YUN_OSS_PATH + "/" + aLiYunImgPath);
                                                                        }
                                                                    }), RequestOptions.DEFAULT);
                                                    imgFile.delete();
                                                    break;
                                                }
                                                TimeUnit.SECONDS.sleep(6);
                                            }
                                        } catch (IOException | InterruptedException e) {
                                            log.warn("文件解析上传异常", e);
                                        }
                                    }
                                });
                            });
                        } else {
                            restHighLevelClient.index(new IndexRequest().index(bin_lin_video.name())
                                    .id(videos.getId()).source(
                                            new HashMap<String, Object>(10) {
                                                {
                                                    put("beginTime", "");
                                                    put("endTime", "");
                                                    put("text", "");
                                                    put("videoDesc", videos.getVideoDesc());
                                                    put("videoId", videos.getId());
                                                    put("videoPage", videos.getVideoPath());
                                                    put("videoCategory", videos.getVideoCategory());
                                                    put("audioId", videos.getAudioId());
                                                    put("userId", videos.getUserId());
                                                    put("gitURL", "");
                                                }
                                            }), RequestOptions.DEFAULT);
                        }
                        wavFile.delete();
                        break;
                    }
                    TimeUnit.SECONDS.sleep(6);
                }
                log.debug("解析完成");
            } catch (Exception e) {
                log.warn("文件解析异常", e);
            }
        });
    }

    /**
     * @param userId
     * @param bgmId
     * @param desc
     * @param videoCategory
     * @param videoFilter
     * @param file
     * @return
     */
    @ApiOperation(value = "用户上传视频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "bgmId", value = "背景音乐id", required = false, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "desc", value = "视频描述", required = false, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoCategory", value = "视频类别(kuaisou快手抖音...)", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoFilter", value = "视频滤镜(light高亮,black黑色,define默认)", required = true, dataType = "String", paramType = "form")

    })
    @PostMapping(value = "/upload")
    public XyfJsonResult uploadFace(String userId, String bgmId,
                                    String desc, String videoCategory, String videoFilter,
                                    @ApiParam(value = "视频文件", required = true) MultipartFile file) {

        if (StringUtils.isBlank(userId)) {
            return XyfJsonResult.errorException("用户id不能为空...");
        }
        // 文件保存命名空间

        // 保存到数据库路径 相对路径
        String id = IdUtil.fastSimpleUUID();
        String uploadPathDB = "/" + userId + "/video/" + id + "." + FileUtil.getSuffix(file.getOriginalFilename());
        String coverPath = "/" + userId + "/video/" + id +".jpg";

        String videoAbsolutePath = FILe_SPACE + uploadPathDB;
        String coverAbsolutePath = FILe_SPACE + coverPath;
        try {
            file.transferTo(new File(videoAbsolutePath));
            VideoUtils.getVideoJpgByTime(1000, videoAbsolutePath, coverAbsolutePath);
        } catch (IOException e) {
            log.warn("视频上传或视频封面解析异常", e);
        }
        // 保存视频到数据库
        Videos video = new Videos();
        video.setVideoCategory(videoCategory);
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoSeconds(null);
        video.setVideoHeight(null);
        video.setVideoWidth(null);
        video.setVideoDesc(desc);
        video.setCreateTime(new Date());
        video.setVideoPath(uploadPathDB);
        video.setCoverPath(coverPath);
        video.setLikeCounts(0L);
        video.setVideoFileter(videoFilter);
        video.setStatus(VideoStatusEnum.Success.value);

        String videoId = videoService.saveVideo(video);
        video.setId(videoId);
        parseFile(video);
        redisUtils.sSet(VIDEO_ID_SET.name(), videoId);
        // 返回200
        return XyfJsonResult.ok(videoId);

    }
    /**
     * 分页和搜索查询列表 issave=1 需要保存 issave=0 不需要保存 或者为空的时候
     *
     * @return
     */
    @ApiOperation(value = "获取热搜关键词")
    @PostMapping(value = "/hot") // isSaveRecord 是否保存记录
    public XyfJsonResult hot() {
        return XyfJsonResult.ok(videoService.getHotWords());
    }

    @ApiOperation(value = "查询所有视频")
    @PostMapping(value = "/showAll") // isSaveRecord 是否保存记录
    public XyfJsonResult showAll(@RequestBody Videos video, Integer isSaveRecord, Integer page, String category) {
        if (page == null) {
            page = 1;
        }
        PageResult pageResult = videoService.getAllVideos(video, isSaveRecord, page, PAGE_SIZE, category);
        System.out.println(pageResult.toString());
        return XyfJsonResult.ok(pageResult);
    }

    @ApiOperation(value = "查询随机视频")
    @GetMapping("/rand/{num}")
    public XyfJsonResult rand(@PathVariable("num") int num) {
        return XyfJsonResult.ok(videoService.getVideos(redisUtils.sGetRand(VIDEO_ID_SET.name(), num > 10 ? 10 : num)));
    }

    @PostMapping(value = "/userLike") // isSaveRecord 是否保存记录
    public XyfJsonResult userLike(String userId, String videoId, String videoCreaterId) {
        videoService.userLikeVideo(userId, videoId, videoCreaterId);
        return XyfJsonResult.ok();
    }

    @PostMapping(value = "/userUnLike") // isSaveRecord 是否保存记录
    public XyfJsonResult userUnLike(String userId, String videoId, String videoCreaterId) {
        videoService.userUnLikeVideo(userId, videoId, videoCreaterId);
        return XyfJsonResult.ok();
    }

    // 保存用户的评论到数据库
    @PostMapping(value = "/saveComments") // isSaveRecord 是否保存记录
    public XyfJsonResult saveComments(String userId, String videoId, String comment) {
        videoService.saveComment(userId, videoId, comment);
        return XyfJsonResult.ok();
    }

    // 保存用户的评论到数据库
    @PostMapping(value = "/queryCommentsByVideoId")
    public XyfJsonResult queryCommentsByVideoId(String videoId) {
        List<CommentsVo> commentsAll = videoService.queryCommentsByVideoId(videoId);
        return XyfJsonResult.ok(commentsAll);// 返回当前视频的所有评论
    }

    // 查询当前用户的所有的视频
    @PostMapping(value = "/queryVideosByUser")
    public XyfJsonResult queryVideosByUser(String userId) {
        List<VideosVo> listVideos = videoService.queryVideosByUser(userId);
        return XyfJsonResult.ok(listVideos);
    }

    /**
     * @param userId      举报人的id
     * @param dealUserId  被举报用户的id
     * @param dealVideoId 被举报视频的id
     * @return
     */
    // 举报视频
    @PostMapping("/report")
    public XyfJsonResult reportVideosByUser(String userId, String dealUserId, String dealVideoId, String title,
                                            String content) {
        videoService.reportVideoByUser(dealUserId, dealVideoId, userId, title, content);
        return XyfJsonResult.ok();
    }

}
