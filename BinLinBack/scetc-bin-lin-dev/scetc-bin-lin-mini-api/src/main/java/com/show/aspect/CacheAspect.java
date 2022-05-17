package com.show.aspect;


import com.show.other.RedisUtils;
import com.show.other.SearchCondition;
import com.show.pojo.Users;
import com.show.vo.UsersVo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

import static com.show.other.AppConst.Redis.SEARCH_HISTORY;
import static com.show.other.AppConst.Redis.SEARCH_TEXT;


/**
 * @author 916202420@qq.com
 * @date 2022/4/16 21:45
 */
@Aspect
@Component
@Slf4j
public class CacheAspect {
    @Autowired
    private RedisUtils redisUtils;

    //region 添加搜索文本

    @Async
    public void asyncAddSearchText(String searchText, HttpServletRequest request) {
        try {
            Object user = request.getSession().getAttribute("user");
            if (user instanceof Users) {
                Users usersVo = (Users) user;
                String userId = usersVo.getId();
                if (StringUtil.isNotEmpty(userId)) {
                    // 添加搜索历史
                    redisUtils.sSet(SEARCH_HISTORY.name()+ ":" + userId, searchText);
                }
            }
            // 自增
            redisUtils.zIncrementScore(SEARCH_TEXT.name(), searchText, 1);
        } catch (Exception e) {
            log.debug("添加搜索文本异常", e);
        }
    }

    @Before(value = "execution(* com.show.controller.*.*(..,com.show.other.SearchCondition,javax.servlet.http.HttpServletRequest,..))")
    public void addSearchText(JoinPoint jp) {
        Object[] args = jp.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof SearchCondition) {
                SearchCondition condition = (SearchCondition) args[i];
                String searchText = condition.getSearchText();
                if (StringUtil.isNotEmpty(searchText)) {
                    searchText = searchText.trim();
                    asyncAddSearchText(searchText, (HttpServletRequest) args[i+1]);
                }
            }
        }
    }

    //endregion


    private void clearCache(Integer returnVal, String... keys) {
        if (returnVal > 0) {
            for (String key : keys) {
                redisUtils.del(key);
            }
        }
    }

    private Object addCache(String key, ProceedingJoinPoint pjp) throws Throwable {
        Object cache = getCache(key);
        if (cache != null) {
            return cache;
        }
        Object returnVal = pjp.proceed();
        redisUtils.set(key, returnVal);
        return returnVal;
    }

    private Object getCache(String key) {
        return redisUtils.get(key);
    }
}