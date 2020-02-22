package com.leyou.controller;

import com.leyou.service.GoodsHtmlService;
import com.leyou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@org.springframework.stereotype.Controller
public class GoodsController {
    @Autowired
    private GoodsHtmlService goodsHtmlService;
    @Autowired
    private GoodsService goodsService;
    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id")Long id, Model model){
        Map<String, Object> stringObjectMap = goodsService.querySpuBySpuId(id);
        model.addAllAttributes(stringObjectMap);
        goodsHtmlService.asyncExcute(id);
        return "item";
    }
}
