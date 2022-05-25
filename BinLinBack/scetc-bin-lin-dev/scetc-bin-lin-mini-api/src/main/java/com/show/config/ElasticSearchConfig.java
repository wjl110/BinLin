package com.show.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static com.show.other.AppConst.ElasticSearch.bin_lin_video;

/**
 * @author 916202420@qq.com
 * @date 2022/5/15 21:59
 */
@Configuration
@ConfigurationProperties("elasticsearch")
@Data
@Slf4j
public class ElasticSearchConfig {

    private String host;
    private Integer port;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(
                        //若有多个，可以传一个数组
                        new HttpHost(host, port, "http")));
        try {
//                restHighLevelClient.indices().delete(new DeleteIndexRequest(bin_lin_video.name()), RequestOptions.DEFAULT);
            if (!restHighLevelClient.indices().exists(new GetIndexRequest(bin_lin_video.name()), RequestOptions.DEFAULT)) {
                XContentBuilder builder = XContentFactory.jsonBuilder();
                builder.startObject();
                {
                    builder.startObject("properties");
                    {
                        builder.startObject("text");
                        {
                            builder.field("type", "text")
                                    .field("analyzer", "ik_max_word")
                                    .field("search_analyzer", "ik_smart");
                        }
                        builder.endObject();

                        builder.startObject("videoDesc");
                        {
                            builder.field("type", "text")
                                    .field("analyzer", "ik_max_word")
                                    .field("search_analyzer", "ik_smart");
                        }
                        builder.endObject();
                    }
                    builder.endObject();
                }
                builder.endObject();
                restHighLevelClient.indices().create(new CreateIndexRequest(bin_lin_video.name())
                        .mapping(builder), RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            log.warn("ElasticSearch索引创建异常", e);
        }
        return restHighLevelClient;
    }
}