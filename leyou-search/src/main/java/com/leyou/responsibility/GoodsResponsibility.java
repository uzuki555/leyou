package com.leyou.responsibility;

import com.leyou.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsResponsibility extends ElasticsearchRepository<Goods,Long> {
}
