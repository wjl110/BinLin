package com.show.controller;

import cn.hutool.core.util.StrUtil;
import com.show.other.AppConst;
import com.show.other.RedisUtils;
import com.show.other.SearchCondition;
import com.show.pojo.Users;
import com.show.utils.XyfJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.show.other.AppConst.Redis.SEARCH_HISTORY;
import static com.show.other.AppConst.Redis.SEARCH_TEXT;

/**
 * @author 916202420@qq.com
 * @date 2022/5/15 15:17
 */
@RestController
@Api(tags = {"搜索接口"})
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @ApiOperation("搜索视频")
    @PostMapping("/video")
    public XyfJsonResult search(@RequestBody SearchCondition searchCondition, HttpServletRequest request) throws IOException {
        if (StrUtil.isBlank(searchCondition.getSearchText())) {
            return XyfJsonResult.errorMsg("搜索文本不能为空");
        }
        return XyfJsonResult.ok(
                restHighLevelClient.search(new SearchRequest()
                        .indices(AppConst.ElasticSearch.bin_lin_video.name())
                        .source(new SearchSourceBuilder().query(
                                QueryBuilders.boolQuery()
                                        .should(QueryBuilders.fuzzyQuery("videoDesc", searchCondition.getSearchText()))
                                        .should(QueryBuilders.fuzzyQuery("text", searchCondition.getSearchText())))
                                .from((searchCondition.getCurrentPage() - 1) / searchCondition.getPageSize())
                                .size(searchCondition.getPageSize()))
                , RequestOptions.DEFAULT).getHits());
    }

    @GetMapping("/history")
    @ApiOperation("获取搜索历史")
    public XyfJsonResult getSearchHistory(HttpServletRequest request) {
        Object user = request.getSession().getAttribute("user");
        Set<Object> set = new HashSet<>();
        if (user instanceof Users) {
            String id = ((Users) user).getId();
            if (StringUtil.isNotEmpty(id)) {
                set = redisUtils.sGet(SEARCH_HISTORY.name() + ":" + id);
            }
        }
        return XyfJsonResult.ok(set);
    }

    @DeleteMapping("/history/{text}")
    @ApiOperation("删除指定搜索历史")
    public XyfJsonResult deleteSearchHistory(@RequestParam("text") String text, HttpServletRequest request) {
        Object user = request.getSession().getAttribute("user");
        if (user instanceof Users) {
            String id = ((Users) user).getId();
            if (StringUtil.isNotEmpty(id)) {
                redisUtils.zRemove(SEARCH_HISTORY.name() + ":" + id, text);
            }
        }
        return XyfJsonResult.ok("删除成功");
    }

    /**
     * 获取热搜
     * @return
     */
    @GetMapping("/hot/{num}")
    @ApiOperation("获取热门搜索")
    @ApiImplicitParam
    public XyfJsonResult getSearchHot(@PathVariable("num") Integer num) {
        return XyfJsonResult.ok(redisUtils.zGetReverse(SEARCH_TEXT.name(), num > 20 ? 20 : num));
    }

    @DeleteMapping("/history")
    @ApiOperation("清除所有搜索历史")
    public XyfJsonResult clearSearchHistory(HttpServletRequest request) {
        Object user = request.getSession().getAttribute("user");
        if (user instanceof Users) {
            String id = ((Users) user).getId();
            if (StringUtil.isNotEmpty(id)) {
                redisUtils.del(SEARCH_HISTORY.name() + ":" + id);
            }
        }
        return XyfJsonResult.ok("清除成功");
    }
}