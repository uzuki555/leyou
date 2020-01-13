package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtil.isEmpty(key)){
            criteria.andLike("name","%"+key+"%").orEqualTo("letter",key);
        }

        PageHelper.startPage(page,rows);
        if(StringUtils.isNotBlank(sortBy + "desc")){
            example.setOrderByClause(sortBy + " " +(desc ? "desc":"asc"));
        }
        List<Brand> brands = this.brandMapper.selectByExample(example);
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        return  new PageResult<>(pageInfo.getTotal(),pageInfo.getList());
    }
    @Transactional
    public void saveBrand(Brand brand,List<Long> cids) {

        Boolean flag = this.brandMapper.insertSelective(brand) == 1;

        if(flag){
            cids.forEach(cid ->{
                this.brandMapper.insertCategoryAndBrand(cid,brand.getId());
            });
        }

    }

    public List<Brand> queryBrandsByCid(Long cid) {

        return this.brandMapper.SelectByCategoryId(cid);
    }
}
