package com.show.other;

import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * @author 916202420@qq.com
 * @date 2022/4/11 19:21
 */
public class AppConst {

    /**
     * ElasticSearch 常量
     */
    public enum ElasticSearch {
        bin_lin_video
    }

    /**
     * Redis 常量
     */
    public enum Redis {
        LOGIN, ALL_CLASSIFY, ALL_PERMISSION, BOTTOM_CLASSIFY, SEARCH_TEXT, SEARCH_HISTORY, VIDEO_ID_SET
    }

    /**
     * Session 常量
     */
    public enum Session {
        CAPTCHA, EMAIL_CAPTCHA
    }

    /**
     * 默认值常量
     */
    public enum Default {
        DEFAULT_HEAD_FILE
    }

    public static class Api {
        public static final String V = "/v";
    }

    public static ExecutorService executorService = new ThreadPoolExecutor(
            10,
            20,
            5,
            TimeUnit.MINUTES,
            new LinkedBlockingQueue<>()
    );
}