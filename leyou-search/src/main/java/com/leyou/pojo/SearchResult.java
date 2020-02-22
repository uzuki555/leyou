package com.leyou.pojo;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchResult  extends PageResult<Goods> {
    private List<Brand> brandList;
    private List<Map<String,Object>> categories ;
    private List<Map<String,Object>> specs;

    public SearchResult(List<Brand> brandList, List<Map<String, Object>> categories, List<Map<String, Object>> specs) {
        this.brandList = brandList;
        this.categories = categories;
        this.specs = specs;
    }

    public SearchResult(Long total, List<Goods> items, List<Brand> brandList, List<Map<String, Object>> categories, List<Map<String, Object>> specs) {
        super(total, items);
        this.brandList = brandList;
        this.categories = categories;
        this.specs = specs;
    }

    public SearchResult(Long total, Integer totalPage, List<Goods> items, List<Brand> brandList, List<Map<String, Object>> categories, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.brandList = brandList;
        this.categories = categories;
        this.specs = specs;
    }

    public List<Map<String, Object>> getSpecs() {
        return specs;
    }

    public void setSpecs(List<Map<String, Object>> specs) {
        this.specs = specs;
    }

    public List<Brand> getBrandList() {
        return brandList;
    }

    public void setBrandList(List<Brand> brandList) {
        this.brandList = brandList;
    }

    public List<Map<String, Object>> getCategories() {
        return categories;
    }

    public void setCategories(List<Map<String, Object>> categories) {
        this.categories = categories;
    }
}
