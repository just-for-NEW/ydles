package com.ydles.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.ydles.search.pojo.SkuInfo;
import com.ydles.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.swing.text.Highlighter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * es搜索实现
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;
    @Override
    public Map search(Map<String, String> searchMap) {
        // 结果map
        Map<String,Object> resultMap = new HashMap<>();
        // 构建查询
        if (searchMap != null){
            // 条件构建对象
            NativeSearchQueryBuilder nativeSearchQueryBuilder=new NativeSearchQueryBuilder();

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            // 关键字查询
            if (StringUtils.isNotEmpty(searchMap.get("keywords"))){
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", searchMap.get("keywords")).operator(Operator.AND);
                boolQueryBuilder.must(matchQueryBuilder);
            }

            // 品牌查询
            if (StringUtils.isNotEmpty(searchMap.get("brand"))){

                TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("brandName", searchMap.get("brand"));
                // term查询一般放到 filter
                boolQueryBuilder.filter(termQueryBuilder);
            }

            // 规格查询
                for (String key : searchMap.keySet()) {
                    if(key.startsWith("spec_")){
                        String value = searchMap.get(key).replace("%2B","+");
                        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword", value);
                        boolQueryBuilder.filter(termQueryBuilder);
                    }
                }

                // 价格区间查询 price= 最低-最高  price = 最低
            if (StringUtils.isNotEmpty(searchMap.get("price"))){
                String price = searchMap.get("price");
                String[] split = price.split("-");
                // price = 最低
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price").gte(split[0]);
                if (split.length == 2){
                    // price= 最低-最高
                    rangeQueryBuilder.lte(split[1]);
                }
                    boolQueryBuilder.filter(rangeQueryBuilder);
            }

            // 搜索
            nativeSearchQueryBuilder.withQuery(boolQueryBuilder);

            // 聚合 品牌
            String skuBrand = "skuBrand";
            TermsAggregationBuilder brandTerms = AggregationBuilders.terms(skuBrand).field("brandName");
            nativeSearchQueryBuilder.addAggregation(brandTerms);

            // 聚合 规格
            String skuSpec = "skuSpec";
            TermsAggregationBuilder specTerms = AggregationBuilders.terms(skuSpec).field("spec.keyword");
            nativeSearchQueryBuilder.addAggregation(specTerms);

            // 分页
            String pageNum = searchMap.get("pageNum");
            String pageSize = searchMap.get("pageSize");
            // 如果请求没有页码，默认分配为1
            if (StringUtils.isEmpty(pageNum)){
                pageNum = "1";
            }
            // 如果请求没有大小，默认分配为30
            if (StringUtils.isEmpty(pageSize)){
                pageSize = "30";
            }
            // 分配页码和大小
            PageRequest pageRequest = PageRequest.of(Integer.parseInt(pageNum)-1, Integer.parseInt(pageSize));
            nativeSearchQueryBuilder.withPageable(pageRequest);

            // 排序
            if (StringUtils.isNotEmpty(searchMap.get("sortField"))&&StringUtils.isNotEmpty(searchMap.get("sortRule"))){
                // 升序
                if (searchMap.get("sortRule").equals("ASC")){
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(searchMap.get("sortField")).order(SortOrder.ASC));
                    // 降序
                }else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(searchMap.get("sortField")).order(SortOrder.DESC));
                }
            }

            // 设置高亮
            HighlightBuilder.Field field = new HighlightBuilder.Field("name");
            // 前签
            field.preTags("<span style='color:red'>");
            // 后签
            field.postTags("/span");
            nativeSearchQueryBuilder.withHighlightFields(field);


            //开启查询
            /**
             * 条件构建对象
             * 查询实体类
             * 查询结果对象
             */
            AggregatedPage<SkuInfo> resultInfo = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
                // 搜索结果和对象如何映射
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                    // 查询结果操作
                    List<T> list = new ArrayList<>();

                    // 获取结果
                    SearchHits hits = searchResponse.getHits();
                    long totalHits = hits.getTotalHits();

                    SearchHit[] hits1 = hits.getHits();
                    for (SearchHit hit : hits1) {
                        // hit转成skuInfo对象
                        String sourceAsString = hit.getSourceAsString();
                        SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);

                        // 获取高亮 设置到name属性中
                        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                        HighlightField highlightField = highlightFields.get("name");
                        Text[] fragments = highlightField.getFragments();
                        StringBuilder realName = new StringBuilder();
                        for (Text fragment : fragments) {
                            realName.append(fragment);
                        }
                        skuInfo.setName(realName.toString());

                        list.add((T) skuInfo);
                    }
                    // 构建返回对象
                    return new AggregatedPageImpl<>(list,pageable,totalHits,searchResponse.getAggregations());
                }
            });

            // 封装最终返回结果
            // 总记录数
            resultMap.put("total",resultInfo.getTotalElements());
            // 总页数
            resultMap.put("totalPages",resultInfo.getTotalPages());
            // 数据集合
            resultMap.put("rows" , resultInfo.getContent());

            // 把品牌聚合结果前端返回
            StringTerms brandStringTerms  = (StringTerms) resultInfo.getAggregation(skuBrand);
            List<String> brandList = brandStringTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            resultMap.put("brandList",brandList);

            // 把规格聚合结果前端返回
            StringTerms specStringTerms  = (StringTerms) resultInfo.getAggregation(skuSpec);
            List<String> specList = specStringTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());

            resultMap.put("specList",formatSpec(specList));

            // 当前页 每页多少
            resultMap.put("pageNum",pageNum);
            resultMap.put("pageSize",pageSize);

        }
        return resultMap;
    }

    /**
     * 规格转换的方法
     */
    public Map<String, Set<String>> formatSpec(List<String> specList){
        Map<String,Set<String>> resultMap = new HashMap<>();
        // 遍历list
        for (String specStr : specList) {
            Map<String,String> specMap = JSON.parseObject(specStr, Map.class);

            // 遍历map
            for (String key : specMap.keySet()) {
                Set<String> valueSet = resultMap.get(key);
                if (valueSet == null){
                    valueSet = new HashSet<>();
                }
                valueSet.add(specMap.get(key));

                resultMap.put(key,valueSet);
            }
        }
        return resultMap;
    }
}
