package com.ziqingwang.feature.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class RecipeDataService {
    @Autowired
    private RestTemplate restTemplate;

    private static final String RECIPE_DETAIL = "https://ce5.midea.com/recipe-service/v2/recipe/detail?recipeCode={recipeCode}";

    public JSONObject recipeDetail(String recipeCode){
        return restTemplate.getForObject(RECIPE_DETAIL.replace("{recipeCode}", recipeCode), JSONObject.class);
    }
}
