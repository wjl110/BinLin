package com.show.controller;

import java.util.List;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.show.pojo.Category;
import com.show.service.CategoryService;
import com.show.utils.XyfJsonResult;

/**
 * @author 916202420@qq.com 创建时间：2018年6月11日 下午4:14:49
 */
@RestController
@RequestMapping("/category")
@Api(tags = { "专栏接口" })
public class CategoryController extends BasicController {
	
	@Autowired
	private CategoryService categoryService;
	@GetMapping("queryAll")
	public XyfJsonResult queryAll()
	{
		List<Category> list=categoryService.queryCategroyList();
		return XyfJsonResult.ok(list);
	}

}
