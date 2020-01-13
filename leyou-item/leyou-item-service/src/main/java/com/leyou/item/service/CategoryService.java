package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;

import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    public List<Category> queryCategoriesByPid(Long pid) {
        Category record = new Category();
        record.setParentId(pid);
        return  this.categoryMapper.select(record);

    }
    public List<String> queryNamesByIds(List<Long> ids){
        List<Category> categoryList = this.categoryMapper.selectByIdList(ids);
        return categoryList.stream().map(category -> {

            return category.getName();
        }).collect(Collectors.toList());
    }
}
