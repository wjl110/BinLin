package com.show.controller;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
    private VideoService videoService;


    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public static void main(String[] args) {
        Videos video = new Videos();
        video.setVideoPath("/181027HKFNAXK494/video/5e6aaaf5-b8aa-4e87-9ff7-aa7b6c33546a.mp4");
        VideoController controller = new VideoController();
        controller.FILe_SPACE = "D:/show_videos_dev";
        controller.parseFile(video);
    }

    @Async
    public void parseFile(Videos videos) {
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
                            if (o instanceof JSONObject) {
                                JSONObject jsonObject = (JSONObject) o;
                                Integer endTime = jsonObject.getInteger("EndTime");
                                Integer beginTime = jsonObject.getInteger("BeginTime");
                                String imgPath = videoPath.substring(0, videoPath.lastIndexOf('.')) + beginTime + ".img";
                                String imgAbsolutePath = FILe_SPACE + imgPath;
                                String aLiYunImgPath = "binlin/user" + imgPath;
                                String text = jsonObject.getString("Text");
                                try {
                                    VideoUtils.getVideoJpgByTime((endTime + beginTime) / 2, videoAbsolutePath, imgAbsolutePath);
                                    for (int j = 0; j < 20; j++) {
                                        File imgFile = new File(imgAbsolutePath);
                                        if (imgFile.isFile()) {
                                            VideoUtils.uploadToALiYun(aLiYunImgPath, new FileInputStream(imgFile));
                                            log.debug("GIF文件已上传->" + aLiYunImgPath);
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
                                            break;
                                        }
                                        imgFile.delete();
                                        TimeUnit.SECONDS.sleep(6);
                                    }
                                } catch (IOException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    break;
                }
                wavFile.delete();
                TimeUnit.SECONDS.sleep(6);
            }
        } catch (Exception e) {
            log.warn("文件解析错误", e);
        }
    }

    /**
     * @param userId
     * @param bgmId
     * @param duration
     * @param tmpWidth
     * @param tmpHeight
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
            @ApiImplicitParam(name = "duration", value = "视频时间", required = true, dataType = "double", paramType = "form"),
            @ApiImplicitParam(name = "tmpWidth", value = "视频宽度", required = true, dataType = "int", paramType = "form"),
            @ApiImplicitParam(name = "tmpHeight", value = "视频高度", required = true, dataType = "int", paramType = "form"),
            @ApiImplicitParam(name = "desc", value = "视频描述", required = false, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoCategory", value = "视频类别(kuaisou快手抖音...)", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoFilter", value = "视频滤镜(light高亮,black黑色,define默认)", required = true, dataType = "String", paramType = "form")

    })
    @PostMapping(value = "/upload")
    public XyfJsonResult uploadFace(String userId, String bgmId, double duration, int tmpWidth, int tmpHeight,
                                    String desc, String videoCategory, String videoFilter,
                                    @ApiParam(value = "视频文件", required = true) MultipartFile file) {

        if (StringUtils.isBlank(userId)) {
            return XyfJsonResult.errorException("用户id不能为空...");
        }
        // 文件保存命名空间

        // 保存到数据库路径 相对路径
        String uploadPathDB = "/" + userId + "/video";
        String coverPath = "/" + userId + "/video";

        String finalVideoPath = "";
        String fileName = null;
        if (file != null) {

            FileOutputStream fileOutputStream = null;
            InputStream inputStream = null;
            fileName = file.getOriginalFilename();
            try {
                if (StringUtils.isNotBlank(fileName)) {
                    // 文件上传的最终保存路径
                    finalVideoPath = FILe_SPACE + "/" + uploadPathDB + "/" + fileName;
                    // 数据库保存路径
                    uploadPathDB += ("/" + fileName);
                    File outFile = new File(finalVideoPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                } else {// 增加校验防止入侵攻击
                    return XyfJsonResult.errorException("上传失败了");

                }
            } catch (IOException e) {
                return XyfJsonResult.errorException("上传失败了");
            } finally {
                // 如果不为空
                // 则flush 关闭
                try {
                    fileOutputStream.flush();
                    IOUtils.closeQuietly(fileOutputStream);
                    IOUtils.closeQuietly(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 判断bgmid是否为空，如果不为空 则需要查询信息，并且合并视频生成新的视频
                // 如果bgmid为空
                MergeVideo tool = new MergeVideo(FFMPEGEXE);
                if (StringUtils.isNotBlank(bgmId)) {
                    Bgm bgm = bgmService.queryBgmById(bgmId);
                    String mp3InputPath = FILe_SPACE + bgm.getPath();// 得到路径
                    // ffmpeg所在地址
                    // ffmpegPath="G:\\ffmepg视频处理方案\\ffmpeg-20180704-3b10bb8-win64-static\\bin\\ffmpeg.exe";
                    String videoInputPath = finalVideoPath;
                    String outPathName = UUID.randomUUID() + ".mp4";
                    uploadPathDB = "/" + userId + "/video" + "/" + outPathName;
                    String outputStream = FILe_SPACE + "/" + uploadPathDB;

                    tool.convertor(mp3InputPath, videoInputPath, duration, outputStream, videoFilter);
                } else {
                    if (!videoFilter.equals(Filter_DEFINE))// 如果当前程序的滤镜不为define
                    // 则给视频增加滤镜
                    {
                        String videoInputPath = finalVideoPath;
                        String outPathName = UUID.randomUUID() + ".mp4";
                        uploadPathDB = "/" + userId + "/video" + "/" + outPathName;
                        String outputStream = FILe_SPACE + "/" + uploadPathDB;
                        tool.convertor(null, videoInputPath, duration, outputStream, videoFilter);
                    }
                }
            }
        }
        FetchVideo videoInfo = new FetchVideo(FFMPEGEXE);
        String fileNamePrefix = fileName.split("\\.")[0];
        // 相对路径放到数据库里
        coverPath = coverPath + "/" + fileNamePrefix + UUID.randomUUID() + ".jpg";
        // 输出转换后的图片
        videoInfo.getcover(finalVideoPath, FILe_SPACE + coverPath);

        // 保存视频到数据库
        Videos video = new Videos();
        video.setVideoCategory(videoCategory);
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoSeconds((float) duration);
        video.setVideoHeight(tmpHeight);
        video.setVideoWidth(tmpWidth);
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
        // 返回200
        return XyfJsonResult.ok(videoId);

    }
    /**
     * 用于上传封面，目前已经整合到·上传视频的接口中
     */
//	@ApiOperation(value = "上传封面", notes = "上传封面的接口")
//	@ApiImplicitParams({
//			@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
//			@ApiImplicitParam(name = "videoId", value = "视频主键id", required = true, dataType = "String", paramType = "form") })
//	@PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
//	public XyfJsonResult uploadCover(String userId, String videoId,
//			@ApiParam(value = "视频封面", required = true) MultipartFile file) throws Exception {
//
//		if (StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)) {
//			return XyfJsonResult.errorMsg("视频主键id和用户id不能为空...");
//		}
//
//		// 文件保存的命名空间
//		// 保存到数据库中的相对路径
//		String uploadPathDB = "/" + userId + "/video";
//
//		FileOutputStream fileOutputStream = null;
//		InputStream inputStream = null;
//		// 文件上传的最终保存路径
//		String finalCoverPath = "";
//		try {
//			if (file != null) {
//
//				String fileName = file.getOriginalFilename();
//				if (StringUtils.isNotBlank(fileName)) {
//
//					finalCoverPath = FILe_SPACE + uploadPathDB + "/" + fileName;
//					// 设置数据库保存的路径
//					uploadPathDB += ("/" + fileName);
//
//					File outFile = new File(finalCoverPath);
//					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
//						// 创建父文件夹
//						outFile.getParentFile().mkdirs();
//					}
//
//					fileOutputStream = new FileOutputStream(outFile);
//					inputStream = file.getInputStream();
//					IOUtils.copy(inputStream, fileOutputStream);
//				}
//
//			} else {
//				return XyfJsonResult.errorMsg("上传出错...");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return XyfJsonResult.errorMsg("上传出错...");
//		} finally {
//			if (fileOutputStream != null) {
//				fileOutputStream.flush();
//				fileOutputStream.close();
//			}
//		}
//
//		videoService.updateVideo(videoId, uploadPathDB);
//
//		return XyfJsonResult.ok();
//	}

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
