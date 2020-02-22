package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


public interface CategoryApi {



    @GetMapping("list")
    public List<Category> queryCategoriesByPid(@RequestParam(value="pid",defaultValue = "0")Long pid);
    @GetMapping
    public List<String> queryNamesByIds(@RequestParam("ids") List<Long> ids);
}
