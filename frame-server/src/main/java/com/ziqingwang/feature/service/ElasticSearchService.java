package com.ziqingwang.feature.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ElasticSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private RecipeDataService recipeDataService;

    public void index(){
        try{
            JSONObject detail = recipeDataService.recipeDetail("10010");
            IndexRequest indexRequest = new IndexRequest("recipe")
                    .id("10010")
                    .source(detail);
            IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }catch (Exception e){
            log.error("[elasticSearch] - index error, msg:{}", e);
        }
    }

    public void get(){
        try{
            GetRequest getRequest = new GetRequest("recipe", "10010");
            GetResponse resp = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            log.warn("[get] data:{}", resp);
        }catch (Exception e){
            log.error("[elasticSearch] - index error, msg:{}", e);
        }
    }


}
