package com.leyou.service;

import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodsService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;


    public Map<String,Object>  querySpuBySpuId(Long spuId){
        Map<String,Object> spuMap = new HashMap<>();
        Spu spu = this.goodsClient.querySpuById(spuId);
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spuId);
        List<Long> cids = Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3());

        List<String> categoriesName = this.categoryClient.queryNamesByIds(cids);
        List<Map<String,Object>> categories = new  ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String,Object> categoriesMap =new HashMap<>();
            categoriesMap.put("id",cids.get(i));
            categoriesMap.put("name",categoriesName.get(i));
            categories.add(categoriesMap);
        }
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        List<Sku> skus = this.goodsClient.querySkusBySpuId(spuId);
        List<SpecGroup> groups = this.specificationClient.querySpecGroupWithParamsByCid(spu.getCid3());
        Map<Long,String> paramMap = new HashMap<>();
        List<SpecParam> specParams = this.specificationClient.queryParams(null, spu.getCid3(), false, null);
        specParams.forEach(specParam ->{
            paramMap.put(specParam.getId(),specParam.getName());
        });
        spuMap.put("spu",spu);
        spuMap.put("categories",categories);
        spuMap.put("brand",brand);
        spuMap.put("skus",skus);
        spuMap.put("spuDetail",spuDetail);
        spuMap.put("groups",groups);
        spuMap.put("paramMap",paramMap);


        return spuMap;
    }




}
