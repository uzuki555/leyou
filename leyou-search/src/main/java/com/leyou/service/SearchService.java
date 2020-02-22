package com.leyou.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.*;
import com.leyou.pojo.Goods;
import com.leyou.pojo.SearchRequest;
import com.leyou.pojo.SearchResult;
import com.leyou.responsibility.GoodsResponsibility;
import com.netflix.discovery.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsResponsibility goodsResponsibility;

    private  static  final ObjectMapper mapper = new ObjectMapper();


    public SearchResult search(SearchRequest searchRequest){
        String key = searchRequest.getKey();
        if(StringUtils.isBlank(key)){
            return  null;
        }
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
//        QueryBuilder queryBuilder = QueryBuilders.matchQuery("all", key).operator(Operator.AND);
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(searchRequest);
        NativeSearchQueryBuilder searchQuery = nativeSearchQueryBuilder.withQuery(boolQueryBuilder)
                .withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null))
                .withPageable(PageRequest.of(searchRequest.getPage() - 1, searchRequest.getSize()))
                .addAggregation(AggregationBuilders.terms("categoryAgg").field("cid3"))
                .addAggregation(AggregationBuilders.terms("brandAgg").field("brandId"));



        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)goodsResponsibility.search(searchQuery.build());
        Aggregation categoryAgg = goodsPage.getAggregation("categoryAgg");
        Aggregation brandAgg = goodsPage.getAggregation("brandAgg");
        List<Map<String,Object>> categories = this.getCategories(categoryAgg);
        List<Brand> brandList = this.getbrandList(brandAgg);
        List<Map<String,Object>> specs=null;
        if (!CollectionUtils.isEmpty(categories)||categories.size()==1) {
             specs = this.getSpecs(categories.get(0).get("id"), boolQueryBuilder);
        }
        return  new SearchResult(goodsPage.getTotalElements(),goodsPage.getTotalPages(),goodsPage.getContent(),brandList,categories,specs);

    }

    private BoolQueryBuilder getBoolQueryBuilder(SearchRequest searchRequest) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()).operator(Operator.AND));
        Map<String, Object> filter = searchRequest.getFilter();
        for(Map.Entry<String ,Object> entry : filter.entrySet()){
            String key = entry.getKey();
            if (StringUtils.equals(entry.getKey(),"品牌")){
                key = "brandId";
            }else if(StringUtils.equals(entry.getKey(),"分类")){
                key = "cid3";
            }else{
                key="specs."+key+".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        }
        return boolQueryBuilder;

    }

    private List<Map<String, Object>> getSpecs(Object id, QueryBuilder queryBuilder) {
        Long cid = Long.valueOf(String.valueOf(id));
        List<SpecParam> params = specificationClient.queryParams(null, cid, false, true);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
         nativeSearchQueryBuilder.withQuery(queryBuilder);
        params.forEach(specParam -> {
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("parmAgg").field("specs."+specParam.getName()+".keyword"));
        });
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
        AggregatedPage<Goods> search = (AggregatedPage<Goods>)goodsResponsibility.search(nativeSearchQueryBuilder.build());
        Map<String, Aggregation> stringAggregationMap = search.getAggregations().asMap();

        List<Map<String, Object>>  list = new ArrayList<>();
        for(Map.Entry<String,Aggregation> entry : stringAggregationMap.entrySet()){
            Map<String, Object> spec = new HashMap<>();
            Aggregation aggregation = entry.getValue();
            StringTerms stringTerms = (StringTerms)aggregation;
            List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
            List<String> options = buckets.stream().map(bucket -> {
                return bucket.getKeyAsString();
            }).collect(Collectors.toList());
            spec.put("k",entry.getKey());
            spec.put("options",options);
            list.add(spec);

        }
        return  list;
    }

    private List<Brand> getbrandList(Aggregation brandAgg) {
        LongTerms longTerms = (LongTerms)brandAgg;
        return  longTerms.getBuckets().stream().map(bucket -> {
            long longValue = bucket.getKeyAsNumber().longValue();
            Brand brand = brandClient.queryBrandById(longValue);
            return brand;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getCategories(Aggregation categoryAgg) {
        LongTerms longTerms = (LongTerms)categoryAgg;
        return  longTerms.getBuckets().stream().map(bucket -> {
            long longValue = bucket.getKeyAsNumber().longValue();
            String name = categoryClient.queryNamesByIds(Arrays.asList(longValue)).get(0);
            HashMap<String, Object> map = new HashMap<>();
            map.put("id",longValue);
            map.put("name",name);
            return  map;
        }).collect(Collectors.toList());
    }

    public Goods buildGoods(Spu spu){

            Goods goods = new Goods();
            try {
        List<Sku> skus = goodsClient.querySkusBySpuId(spu.getId());
        List <Long> skusPriceList = new ArrayList<Long>();
        List<Map<String,Object>> skuInfoList = new ArrayList<Map<String,Object>>();
        skus.forEach(sku -> {
            Map<String,Object> skuInfo = new HashMap<String,Object>();
            skuInfo.put("id",sku.getId());
            skuInfo.put("title",sku.getTitle());
            skuInfo.put("image",StringUtils.isNotBlank(sku.getImages())?StringUtils.split(sku.getImages()," ")[0]:" ");
            skuInfo.put("price",sku.getPrice());
            skusPriceList.add(sku.getPrice());
            skuInfoList.add(skuInfo);

        });
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        List<String> CategoryNames = categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1()));
        List<SpecParam> specParams = specificationClient.queryParams(null, spu.getCid3(), null, true);
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spu.getId());
        HashMap<String, Object> genericSpec = mapper.readValue(spuDetail.getGenericSpec(), new TypeReference<HashMap<String, String>>() {});
        HashMap<String, List> specialSpec = mapper.readValue(spuDetail.getSpecialSpec(), new TypeReference<HashMap<String, List>>() {});
        HashMap<String, Object> specs =  new HashMap<String,Object>();
        specParams.forEach(specParam -> {
            if(specParam.getGeneric()){
                Object val = genericSpec.get(specParam.getId().toString());
                if(specParam.getNumeric()){
                    val = chooseSegment(val.toString(),specParam);
                }
                specs.put(specParam.getName(),val);
            }else {
                specs.put(specParam.getName(),specialSpec.get(specParam.getId().toString()));
            }
        });
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setPrice(skusPriceList);
        goods.setAll(spu.getSubTitle()+" "+ StringUtils.join(CategoryNames," ")+" "+brand.getName());
        goods.setId(spu.getId());

            goods.setSkus(mapper.writeValueAsString(skuInfoList));

        goods.setSpecs(specs);
        goods.setSubTitle(spu.getSubTitle());
        goods.setBrandId(spu.getBrandId());


        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
            }
        return  goods;
    }

    private String chooseSegment(String value, SpecParam specParam) {

        double number = NumberUtils.toDouble(value);

        for(String segment : specParam.getSegments().split(",")) {
            String[] segs = segment.split("-");
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MIN_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            if (number >= begin && number < end) {
                if (segs.length == 1) {
                return segs[0] + specParam.getUnit() + "以上";
                } else if (begin ==0) {
                return segs[1] + specParam.getUnit() + "以下";
                }
                return segment + specParam.getUnit();
            }
            break;
        }
        return  "其他";
    }

    public void deleteIndex(Long spuId) {
        this.goodsResponsibility.deleteById(spuId);
    }

    public void createIndex(Long spuId) {
        Spu spu = goodsClient.querySpuById(spuId);
        Goods goods = buildGoods(spu);
        this.goodsResponsibility.save(goods);
    }
}
