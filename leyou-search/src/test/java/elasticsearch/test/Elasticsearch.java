package elasticsearch.test;

import com.leyou.client.GoodsClient;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.pojo.Goods;
import com.leyou.responsibility.GoodsResponsibility;
import com.leyou.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Elasticsearch {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private GoodsResponsibility goodsResponsibility;
    @Autowired
    private SearchService searchService;
    @Autowired
    private GoodsClient goodsClient;



    @Test
    private void Test(){
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);
        int rows =100;
        int page = 1;
        do {
            PageResult<SpuBo> pageResult = goodsClient.querySpuByPage(null, null, page, rows);
            List<SpuBo> items = pageResult.getItems();
            List<Goods> goods = items.stream().map(spuBo -> {
                return searchService.buildGoods(spuBo);
            }).collect(Collectors.toList());
            goodsResponsibility.saveAll(goods);
            rows = items.size();
            page++;

        }while (rows ==100);

    }

}
