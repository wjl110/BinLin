package com.show;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.show.domain.UserOpen;
import com.show.service.UserOpenService;
import com.show.service.VideoOpenService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

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

	@Test
	public void test() throws Exception {
		List<UserOpen> list = service.list(new LambdaQueryWrapper());
		System.out.println(list);
	}
	@Autowired
	private UserOpenService service;
}