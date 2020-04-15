package com.ziqingwang.feature.web;

import com.ziqingwang.feature.service.ElasticSearchService;
import com.ziqingwang.feature.support.Response;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "ElasticSearch-API")
public class ElasticSearchHandler {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @PostMapping("index")
    public Response index(String recipeCode){
        elasticSearchService.index(recipeCode);
        return Response.ok();
    }

    @GetMapping("get")
    public Response get(String recipeCode){
        return Response.ok(elasticSearchService.get(recipeCode));
    }

    @GetMapping("query")
    public Response query(String keyword){
        return Response.ok(elasticSearchService.query(keyword));
    }

    @GetMapping("scroll_search")
    public Response scroll_search(String keyword){
        return Response.ok(elasticSearchService.searchScroll(keyword));
    }

    @GetMapping("multi_search")
    public Response multi_search(String keyword){
        return Response.ok(elasticSearchService.multiSearch(keyword));
    }

    @GetMapping("template_search")
    public Response template_search(String keyword){
        return Response.ok(elasticSearchService.templateSearch(keyword));
    }

    @PostMapping("indexAll")
    public Response indexAll(){
        elasticSearchService.indexAll();
        return Response.ok();
    }



}
