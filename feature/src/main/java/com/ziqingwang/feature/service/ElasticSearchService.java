package com.ziqingwang.feature.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ziqingwang.feature.entity.RecipeIndexDTO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ElasticSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private RecipeDataService recipeDataService;

    // todo 1. suggestions 建议
    // todo 2. hightlighting 高亮

    public Object index(String recipeCode){
        IndexResponse indexResponse = null;
        try{
            JSONObject detail = recipeDataService.recipeDetail(recipeCode);
            RecipeIndexDTO recipeIndexDTO = detail.toJavaObject(RecipeIndexDTO.class);
            String jsonStr = JSON.toJSONString(recipeIndexDTO);
            recipeIndexDTO.set_all(jsonStr);
            IndexRequest indexRequest = new IndexRequest("recipe")
                    .id(recipeCode)
                    .source(JSON.parseObject(JSON.toJSONString(recipeIndexDTO)));
            indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }catch (Exception e){ log.error("[elasticSearch] - index error, msg:{}", e);
            log.error("[elasticSearch] - index error, msg:{}", e);
        }
        return indexResponse;
    }

    public void indexAll(){
        try{
            JSONArray allRecipes = recipeDataService.allRecipeDetails();
            allRecipes.stream()
                    .forEach(recipe->{
                        JSONObject o = (JSONObject)recipe;
                        index(o.getString("recipeCode"));
                        log.warn("索引数据：{}", o.getString("recipeName"));
                    });
        }catch (Exception e){
            log.error("[elasticSearch] - index error, msg:{}", e);
        }
    }

    public GetResponse get(String recipeCode){
        GetResponse resp = null;
        try{
            GetRequest getRequest = new GetRequest("recipe", recipeCode);
            resp = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            log.warn("[get] data:{}", resp);
        }catch (Exception e){
            log.error("[elasticSearch] - index error, msg:{}", e);
        }
        return resp;
    }

    public SearchResponse query(String keyword){
        SearchRequest request = new SearchRequest("recipe");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("foods", keyword));
        sourceBuilder.from(0);
        sourceBuilder.size(5);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        SearchResponse resp = null;
        try{
            resp = restHighLevelClient.search(request.source(sourceBuilder), RequestOptions.DEFAULT);
            log.warn("[query] data:{}", resp);
        }catch (Exception e){
            log.error("[elasticSearch] - index error, msg:{}", e);
        }
        return resp;
    }




}
