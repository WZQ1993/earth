package com.ziqingwang.feature.service;

import java.util.Objects;

import com.alibaba.fastjson.JSONArray;
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
    private static final String RECIPE_ALL = "https://ce5.midea.com/recipe-service/v2/admin/recipe/list?applianceType=all";

    public JSONObject recipeDetail(String recipeCode){
        JSONObject resp = restTemplate.getForObject(RECIPE_DETAIL.replace("{recipeCode}", recipeCode), JSONObject.class);
        if(Objects.nonNull(resp) && "0".equals(resp.getString("resCode"))){
            return resp.getJSONObject("data");
        }
        return null;
    }

    public JSONArray allRecipeDetails(){
        JSONObject resp = restTemplate.getForObject(RECIPE_ALL, JSONObject.class);
        if(Objects.nonNull(resp) && "0".equals(resp.getString("resCode"))){
            return resp.getJSONArray("data");
        }
        return null;
    }
}
