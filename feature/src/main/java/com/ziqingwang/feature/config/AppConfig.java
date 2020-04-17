package com.ziqingwang.feature.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

@Configuration
@Data
public class AppConfig {
    @Value("classpath:recipe_detail_index_mapping.json")
    private Resource recipe_detail_mapping_res;
    @Value("classpath:recipe_detail_index_setting.json")
    private Resource recipe_detail_setting_res;
    @Value("${app.search.recipe-detail-index-alias}")
    private String recipe_detail_index = "recipe_detail";

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
