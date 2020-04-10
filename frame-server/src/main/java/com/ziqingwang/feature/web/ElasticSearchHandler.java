package com.ziqingwang.feature.web;

import com.ziqingwang.feature.service.ElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ElasticSearchHandler {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @GetMapping("index")
    public void index(){
        elasticSearchService.index();
    }
}
