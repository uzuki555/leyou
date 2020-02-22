package com.leyou.pojo;

import java.util.Map;

public class SearchRequest {
    private String key;



    private Integer page;
    private static final Integer DEFAULT_SIZE = 20;
    private static final Integer DEFAULT_PAGE = 1;
    private Map<String,Object> filter;
    public Map<String, Object> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, Object> filter) {
        this.filter = filter;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if(page == null){
            return  DEFAULT_PAGE;
        }
        return Math.max(page,DEFAULT_PAGE);
    }

    public void setPage(Integer page) {

        this.page = page;
    }

    public  Integer getSize() {
        return DEFAULT_SIZE;
    }


}
