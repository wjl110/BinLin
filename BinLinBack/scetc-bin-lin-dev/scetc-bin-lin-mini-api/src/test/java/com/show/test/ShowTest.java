package com.show.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.show.other.AppConst;
import com.show.other.SearchCondition;
import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class ShowTest {

	private static Long time;
	@Autowired
	private RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
			RestClient.builder(
					//若有多个，可以传一个数组
					new HttpHost("127.0.0.1", 9200, "http")));
	@Before
	public void before()
	{
		time = System.nanoTime();
	}
	@After
	public void after()
	{
		System.out.println("总耗时：" + (System.nanoTime() - time) / 1000 / 1000 + "毫秒");
	}

	@Test
	public void test() throws Exception {
		SearchCondition searchCondition = new SearchCondition(1,5,"朋友", null);
		SearchResponse search = restHighLevelClient.search(new SearchRequest()
						.indices(AppConst.ElasticSearch.bin_lin_video.name())
						.source(new SearchSourceBuilder().query(
										QueryBuilders.wildcardQuery("text", "*" + searchCondition.getSearchText() + "*"))
								)
				, RequestOptions.DEFAULT);
		System.out.println(search);
	}
}