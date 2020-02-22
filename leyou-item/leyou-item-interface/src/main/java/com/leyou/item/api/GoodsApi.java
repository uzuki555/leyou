package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GoodsApi {
    @GetMapping("spu/page")
    public PageResult<SpuBo> querySpuByPage(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "seleable",required = false)Boolean seleable,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows
    );
    @PostMapping("goods")
    public Void saveGoods(@RequestBody SpuBo spuBo);
    @GetMapping("spu/detail/{spuId}")
    public  SpuDetail querySpuDetailBySpuId(@PathVariable("spuId")Long spuId);
    @GetMapping("sku/list")
    public  List<Sku> querySkusBySpuId(@RequestParam("id")Long spuId);
    @PutMapping("goods")
    public  Void updateGoods(@RequestBody SpuBo spuBo);
    @GetMapping("{id}")
    public  Spu querySpuById(@PathVariable("id")Long id );
}
