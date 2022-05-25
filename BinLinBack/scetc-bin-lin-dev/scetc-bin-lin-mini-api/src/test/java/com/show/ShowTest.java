package com.show;

import com.show.mapper.MoVideosMapper;
import com.show.service.UserOpenService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
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
		System.out.println("总耗时：" + (System.nanoTime() - time) / 1000 / 1000 + "毫秒");
	}

	@Autowired
	private MoVideosMapper videosMapper;
	@Test
	public void test() throws Exception {
		videosMapper.selectByIds(new HashSet<String>() {
			{
				add("1");
				add("2");
			}
		}).forEach(System.out::println);
	}
	@Autowired
	private UserOpenService service;
}