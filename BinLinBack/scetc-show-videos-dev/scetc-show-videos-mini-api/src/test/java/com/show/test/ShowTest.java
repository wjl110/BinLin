package com.show.test;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;

@SpringBootTest
public class ShowTest {

	private static Long time;

	@Before
	public void before()
	{
		time = System.nanoTime();
	}
	@After
	public void after()
	{
		System.out.println("总耗时：" + (System.nanoTime() - time) / 1000 + "毫秒");
	}
	@Test
	public void test() throws Exception {
        final String accessKeyId = "LTAI5t8HP6FKv5uJSq1D8tai";
        final String accessKeySecret = "sK1n2hHJqFwuuHSlKwYWXzVJYTMoZI";
        final String regionId = "cn-shanghai";
        final String endpointName = "cn-shanghai";
        final String product = "nls-filetrans";
        final String domain = "filetrans.cn-shanghai.aliyuncs.com";
        IAcsClient client;
        DefaultProfile.addEndpoint(endpointName, regionId, product, domain);
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);
        /**
         * 创建CommonRequest 设置请求参数
         */
        CommonRequest postRequest = new CommonRequest();
        postRequest.setDomain("filetrans.cn-shanghai.aliyuncs.com"); // 设置域名，固定值。
        postRequest.setVersion("2018-08-17");         // 设置API的版本号，固定值。
        postRequest.setAction("SubmitTask");          // 设置action，固定值。
        postRequest.setProduct("nls-filetrans");      // 设置产品名称，固定值。
// 设置录音文件识别请求参数，以JSON字符串的格式设置到请求Body中。
        JSONObject taskObject = new JSONObject();
        taskObject.put("appkey", "KahBv5btpjbTWT7p");    // 项目的Appkey
        taskObject.put("file_link", "C:\\Users\\91620\\Videos\\b.mp4");  // 设置录音文件的链接
//        taskObject.put(KEY_VERSION, "4.0");  // 新接入请使用4.0版本，已接入（默认2.0）如需维持现状，请注释掉该参数设置。
        String task = taskObject.toJSONString();
        postRequest.putBodyParameter("Task", task);  // 设置以上JSON字符串为Body参数。
        postRequest.setMethod(MethodType.POST);      // 设置为POST方式请求。
/**
 * 提交录音文件识别请求
 */
        String taskId = "";   // 获取录音文件识别请求任务的ID，以供识别结果查询使用。
        CommonResponse postResponse = client.getCommonResponse(postRequest);
        if (postResponse.getHttpStatus() == 200) {
            JSONObject result = JSONObject.parseObject(postResponse.getData());
            String statusText = result.getString("StatusText");
            if ("SUCCESS".equals(statusText)) {
                System.out.println("录音文件识别请求成功响应： " + result.toJSONString());
                taskId = result.getString("TaskId");
            }
            else {
                System.out.println("录音文件识别请求失败： " + result.toJSONString());
                return;
            }
        }
        else {
            System.err.println("录音文件识别请求失败，Http错误码：" + postResponse.getHttpStatus());
            System.err.println("录音文件识别请求失败响应：" + JSONObject.toJSONString(postResponse));
            return;
        }
	}
}
