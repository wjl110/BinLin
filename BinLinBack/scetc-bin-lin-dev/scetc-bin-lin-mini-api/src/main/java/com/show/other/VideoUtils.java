package com.show.other;

/**
 * @author 916202420@qq.com
 * @date 2022/5/14 13:19
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class VideoUtils {

    public static final String A_LI_YUN_OSS_PATH = "https://mo-test.oss-cn-hangzhou.aliyuncs.com";
    // 地域ID，常量，固定值。
    public static final String REGIONID = "cn-shanghai";
    public static final String ENDPOINTNAME = "cn-shanghai";
    public static final String PRODUCT = "nls-filetrans";
    public static final String DOMAIN = "filetrans.cn-shanghai.aliyuncs.com";
    public static final String API_VERSION = "2018-08-17";
    public static final String POST_REQUEST_ACTION = "SubmitTask";
    public static final String GET_REQUEST_ACTION = "GetTaskResult";
    // 请求参数
    public static final String KEY_APP_KEY = "appkey";
    public static final String KEY_FILE_LINK = "file_link";
    public static final String KEY_VERSION = "version";
    public static final String KEY_ENABLE_WORDS = "enable_words";
    // 响应参数
    public static final String KEY_TASK = "Task";
    public static final String KEY_TASK_ID = "TaskId";
    public static final String KEY_STATUS_TEXT = "StatusText";
    public static final String KEY_RESULT = "Result";
    // 状态值
    public static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_RUNNING = "RUNNING";
    private static final String STATUS_QUEUEING = "QUEUEING";
    private static final String accessKeyId = "LTAI5t8HP6FKv5uJSq1D8tai";
    private static final String accessKeySecret = "sK1n2hHJqFwuuHSlKwYWXzVJYTMoZI";
    private static final String appKey = "KahBv5btpjbTWT7p";
    private static final String endpoint = "oss-cn-hangzhou.aliyuncs.com";
    private static final String bucketName = "mo-test";
    private static final String ffmepgPath = "D:/ffmpeg/ffmepg/bin/ffmpeg.exe";
    private IAcsClient client;

    public VideoUtils(String accessKeyId, String accessKeySecret) throws ClientException {
        // 设置endpoint
        DefaultProfile.addEndpoint(ENDPOINTNAME, REGIONID, PRODUCT, DOMAIN);
        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(REGIONID, accessKeyId, accessKeySecret);
        this.client = new DefaultAcsClient(profile);
    }

    public String submitFileTransRequest(String appKey, String fileLink) throws ClientException {
        /**
         * 1. 创建CommonRequest，设置请求参数。
         */
        CommonRequest postRequest = new CommonRequest();
        // 设置域名
        postRequest.setDomain(DOMAIN);
        // 设置API的版本号，格式为YYYY-MM-DD。
        postRequest.setVersion(API_VERSION);
        // 设置action
        postRequest.setAction(POST_REQUEST_ACTION);
        // 设置产品名称
        postRequest.setProduct(PRODUCT);
        /**
         * 2. 设置录音文件识别请求参数，以JSON字符串的格式设置到请求Body中。
         */
        JSONObject taskObject = new JSONObject();
        // 设置appkey
        taskObject.put(KEY_APP_KEY, appKey);
        // 设置音频文件访问链接
        taskObject.put(KEY_FILE_LINK, fileLink);
        // 新接入请使用4.0版本，已接入（默认2.0）如需维持现状，请注释掉该参数设置。
        taskObject.put(KEY_VERSION, "4.0");
        // 设置是否输出词信息，默认为false，开启时需要设置version为4.0及以上。
        taskObject.put(KEY_ENABLE_WORDS, true);
        String task = taskObject.toJSONString();
        // 设置以上JSON字符串为Body参数。
        postRequest.putBodyParameter(KEY_TASK, task);
        // 设置为POST方式的请求。
        postRequest.setMethod(MethodType.POST);
        /**
         * 3. 提交录音文件识别请求，获取录音文件识别请求任务的ID，以供识别结果查询使用。
         */
        String taskId = null;
        CommonResponse postResponse = client.getCommonResponse(postRequest);
        if (postResponse.getHttpStatus() == 200) {
            JSONObject result = JSONObject.parseObject(postResponse.getData());
            String statusText = result.getString(KEY_STATUS_TEXT);
            if (STATUS_SUCCESS.equals(statusText)) {
                taskId = result.getString(KEY_TASK_ID);
            }
        }
        return taskId;
    }

    public String getFileTransResult(String taskId) {
        /**
         * 1. 创建CommonRequest，设置任务ID。
         */
        CommonRequest getRequest = new CommonRequest();
        // 设置域名
        getRequest.setDomain(DOMAIN);
        // 设置API版本
        getRequest.setVersion(API_VERSION);
        // 设置action
        getRequest.setAction(GET_REQUEST_ACTION);
        // 设置产品名称
        getRequest.setProduct(PRODUCT);
        // 设置任务ID为查询参数
        getRequest.putQueryParameter(KEY_TASK_ID, taskId);
        // 设置为GET方式的请求
        getRequest.setMethod(MethodType.GET);
        /**
         * 2. 提交录音文件识别结果查询请求
         * 以轮询的方式进行识别结果的查询，直到服务端返回的状态描述为“SUCCESS”或错误描述，则结束轮询。
         */
        String result = null;
        while (true) {
            try {
                CommonResponse getResponse = client.getCommonResponse(getRequest);
                if (getResponse.getHttpStatus() != 200) {
                    break;
                }
                JSONObject rootObj = JSONObject.parseObject(getResponse.getData());
                String statusText = rootObj.getString(KEY_STATUS_TEXT);
                if (STATUS_RUNNING.equals(statusText) || STATUS_QUEUEING.equals(statusText)) {
                    // 继续轮询，注意设置轮询时间间隔。
                    Thread.sleep(10000);
                } else {
                    // 状态信息为成功，返回识别结果；状态信息为异常，返回空。
                    if (STATUS_SUCCESS.equals(statusText)) {
                        result = rootObj.getString(KEY_RESULT);
                        // 状态信息为成功，但没有识别结果，则可能是由于文件里全是静音、噪音等导致识别为空。
                        if (result == null) {
                            result = "";
                        }
                    }
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 视频转gif图片
     * @param time 毫秒时间
     * @param videoPath 视频地址
     * @param imgPath 图片地址
     * @throws IOException
     */
    public static void getVideoJpgByTime(int time, String videoPath, String imgPath) throws IOException {
        new File(imgPath).delete();
        new ProcessBuilder().command(ffmepgPath, "-ss", format(time), "-i", videoPath, imgPath, "-f", "image2", "-frames:v", "1", imgPath).start();
    }

    /**
     * 获取视频声音
     * @param videoPath
     * @param wavPath
     * @throws IOException
     */
    public static void getWavByVideo(String videoPath, String wavPath) throws IOException {
        new ProcessBuilder().command(ffmepgPath, "-i", videoPath, "-ar", "16000", wavPath).start();
    }

    /**
     * 将相对路径文件上传到阿里云
     *
     * @param fileName    如：a/a.jpg
     * @param inputStream 输入流
     */
    public static void uploadToALiYun(String fileName, InputStream inputStream) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            ossClient.putObject(bucketName, fileName, inputStream);
        } catch (OSSException oe) {
            throw new RuntimeException(oe);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 获取音频的字幕
     *
     * @param wavFileLink 音频链接
     * @return
     * @throws Exception
     */
    public static JSONArray getSentencesByWav(String wavFileLink) throws Exception {
        VideoUtils demo = new VideoUtils(accessKeyId, accessKeySecret);
        // 第一步：提交录音文件识别请求，获取任务ID用于后续的识别结果轮询。
        String taskId = demo.submitFileTransRequest(appKey, wavFileLink);
        if (taskId != null) {
            // 第二步：根据任务ID轮询识别结果。
            String result = demo.getFileTransResult(taskId);
            if (result != null) {
                return JSONObject.parseObject(result).getJSONArray("Sentences");
            }
        }
        return null;
    }

    public static String format(Integer n) {
        n = n / 1000;
        int s = n % 60;
        n = n / 60;
        int m = n % 60;
        n = n / 60;
        int h = n % 60;
        return (h > 9 ? h : "0" + h) + ":" + (m > 9 ? m : "0" + m)+ ":" + (s > 9 ? s : "0" + s);
    }
}