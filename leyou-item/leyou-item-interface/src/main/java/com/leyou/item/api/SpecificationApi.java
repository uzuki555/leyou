package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


public interface SpecificationApi {


    @GetMapping("groups/{cid}")
    public List<SpecGroup> queryGroupsByCid(@PathVariable("cid")Long cid );
    @GetMapping("params")
    public List<SpecParam> queryParams(
            @RequestParam(value = "gid",required = false)Long gid,
            @RequestParam(value = "cid",required = false)Long cid,
            @RequestParam(value = "generic",required = false)Boolean generic,
            @RequestParam(value = "searching",required =false )Boolean searching);
    @GetMapping("group/param/{cid}")
    public List<SpecGroup> querySpecGroupWithParamsByCid(@PathVariable("cid")Long cid);
}
