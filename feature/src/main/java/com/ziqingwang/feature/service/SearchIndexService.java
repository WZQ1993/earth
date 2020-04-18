package com.ziqingwang.feature.service;

import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ziqingwang.feature.config.AppConfig;
import com.ziqingwang.feature.entity.RecipeIndexDTO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据索引服务
 *
 * @author: Ziven
 * @date: 2020/04/17
 **/
@Service
@Slf4j
public class SearchIndexService {
	@Resource(name = "esClient")
	private RestHighLevelClient esClient;
	@Autowired
	private RecipeDataService recipeDataService;
	@Autowired
	private AppConfig appConfig;

	public void index(String recipeCode) {
		try {
			JSONObject data = recipeDataService.recipeDetail(recipeCode);
			if (Objects.isNull(data)) {
				log.warn("[elasticSearch] - index data, data is null:{}", recipeCode);
				return;
			}
			// process detail
			RecipeIndexDTO recipeDetail = data.toJavaObject(RecipeIndexDTO.class);
			RecipeIndexDTO.RecipeInfoVO info = recipeDetail.getInfo();
			List<RecipeIndexDTO.RecipeFoodVO> foods = recipeDetail.getFoods();
			info.setRecipeName_suggest(info.getRecipeName());
			foods.stream().forEach(food -> food.setFoodName_suggest(food.getFoodName()));
			IndexRequest indexRequest = new IndexRequest(appConfig.getRecipe_detail_index())
					.id(recipeCode)
					.source(JSON.parseObject(JSON.toJSONString(recipeDetail)));
			IndexResponse indexResponse = esClient.index(indexRequest, RequestOptions.DEFAULT);
			log.warn("[elasticSearch] - index data, resp:{}", indexResponse);
		} catch (Exception e) {
			log.error("[elasticSearch] - index error, msg:{}", e);
		}
	}

	public void batchIndex(String applianceType) {
		try {
			JSONArray allRecipes = recipeDataService.recipes(applianceType);
			allRecipes.stream()
					.forEach(recipe -> {
						JSONObject o = (JSONObject) recipe;
						index(o.getString("recipeCode"));
						log.warn("索引数据：{}", o.getString("recipeName"));
					});
		} catch (Exception e) {
			log.error("[elasticSearch] - index error, msg:{}", e);
		}
	}
}
