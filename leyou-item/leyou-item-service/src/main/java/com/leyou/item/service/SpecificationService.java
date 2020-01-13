package com.leyou.item.service;


import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper groupMapper;
    @Autowired
    private SpecParamMapper paramMapper;

    public List<SpecGroup> queryGroupsByCid(Long cid) {
//        Example example = new Example(SpecGroup.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("cid",cid);
//        List<SpecGroup> groups = groupMapper.selectByExample(example);
        SpecGroup record = new SpecGroup();
        record.setCid(cid);
        List<SpecGroup> groups = groupMapper.select(record);
        return groups;
    }

    public List<SpecParam> queryParams(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam record = new SpecParam();
        record.setGroupId(gid);
        record.setCid(cid);
        record.setSearching(searching);
        record.setGeneric(generic);
        List<SpecParam> params = paramMapper.select(record);
        return params;
    }
}
