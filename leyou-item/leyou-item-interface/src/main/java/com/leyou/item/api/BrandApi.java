package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


public interface BrandApi {

    @GetMapping("page")
    public PageResult<Brand> queryBrandsByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy ,
            @RequestParam(value = "desc",required = false) Boolean desc
    );
    @PostMapping
    public  Void saveBrand(Brand brand, @RequestParam("cids")List<Long> cids);


    @GetMapping("cid/{cid}")
    public  List<Brand> queryBrandsByCid(@PathVariable("cid")Long cid);
    @GetMapping("{id}")
    public  Brand queryBrandById(@PathVariable("id") Long id );
}
