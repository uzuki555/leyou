package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AmqpTemplate amqpTemplate;

    public PageResult<SpuBo> querySpuByPage(String key, Boolean seleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        if(seleable!=null){
            criteria.andEqualTo("seleable",seleable);
        }
        PageHelper.startPage(page, rows);

        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());

            List<String> names = this.categoryService.queryNamesByIds(Arrays.asList(spuBo.getCid1(), spuBo.getCid2(), spuBo.getCid3()));
            spuBo.setCname(StringUtils.join(names, "-"));
            return spuBo;
        }).collect(Collectors.toList());

        return new PageResult<>(pageInfo.getTotal(),spuBos);
    }
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insertSelective(spuBo);
        spuBo.getSpuDetail().setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuBo.getSpuDetail());
//        for(Sku sku : spuBo.getSkus()) {
//            this.skuMapper.insertSelective(sku);
//        }
        saveSkuAndStock(spuBo);
        sendMessage("insert",spuBo.getId());
    }

    private void sendMessage(String type ,Long id) {
        try {
            this.amqpTemplate.convertAndSend("item-"+type,id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());

            this.skuMapper.insertSelective(sku);
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }

    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return  this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(sku -> {

            sku.setStock(this.stockMapper.selectByPrimaryKey(sku.getId()).getStock());
        });
        return  skus;
    }
    @Transactional
    public void updateGoods(SpuBo spuBo) {
        Sku record = new Sku();
        record.setSpuId(spuBo.getId());
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(sku -> {
            this.stockMapper.deleteByPrimaryKey(sku.getId());
        });
        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());
        this.skuMapper.delete(sku);
        this.saveSkuAndStock(spuBo);
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        spuBo.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spuBo);
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());
        sendMessage("update",spuBo.getId());
    }

    public Spu querySpuById(Long id) {
        return  this.spuMapper.selectByPrimaryKey(id);
    }

    public Sku querySkyBySkuId(Long id) {
        return  this.skuMapper.selectByPrimaryKey(id);
    }
}
