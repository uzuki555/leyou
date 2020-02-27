package com.leyou.cart.service;

import com.leyou.cart.interceptor.CartUserInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.client.GoodsClient;
import com.leyou.item.pojo.Sku;
import com.leyou.pojo.UserInfo;
import com.leyou.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GoodsClient goodsClient;
    public  final String  PREFIX = "USER:CART:";
    public void addCart(Cart cart){

        UserInfo userInfo = CartUserInterceptor.getLoginUser();

        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(PREFIX + userInfo.getId());
        Integer num = cart.getNum();

        if(boundHashOps.hasKey(cart.getSkuId())){
            String cartJson = boundHashOps.get(cart.getSkuId().toString()).toString();
            cart = JsonUtils.parse(cartJson,Cart.class);
            cart.setNum(cart.getNum()+num);
        }else {
            Sku sku = this.goodsClient.querySkuBySkuId(cart.getSkuId());
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(),",")[0]);
            cart.setPrice(sku.getPrice());
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setTitle(sku.getTitle());
            cart.setUserId(userInfo.getId());



        }
        boundHashOps.put(userInfo.getId().toString(),JsonUtils.serialize(cart));

    }

    public List<Cart> queryCarts() {
//        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(PREFIX + CartUserInterceptor.getLoginUser().getId());
//        Map<Object, Object> map = boundHashOps.entries();
//        List<Cart> list = new ArrayList<>();
//        for(Map.Entry<Object,Object> cart : map.entrySet()){
//            list.add(JsonUtils.parse((String)cart.getValue(),Cart.class));
//        }
//        return list;

        String key = PREFIX + CartUserInterceptor.getLoginUser().getId();
        if(!this.redisTemplate.hasKey(key)){
            return null;
        }
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(key);
        List<Object> cartsJson = boundHashOps.values();
        if(CollectionUtils.isEmpty(cartsJson)){
            return null;
        }
        return  cartsJson.stream().map(cart ->{
            return JsonUtils.parse(cart.toString(),Cart.class);
        }).collect(Collectors.toList());
    }
}
