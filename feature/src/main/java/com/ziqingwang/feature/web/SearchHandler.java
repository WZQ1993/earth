package com.ziqingwang.feature.web;

import com.ziqingwang.feature.service.SearchIndexService;
import com.ziqingwang.feature.service.SearchInitService;
import com.ziqingwang.feature.service.SearchSuggestService;
import com.ziqingwang.feature.support.Response;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ziqingwang.feature.web.Api.*;

@RestController
@Api(tags = "ElasticSearch-API")
public class SearchHandler {

    @Autowired
    private SearchInitService searchInitService;
    @Autowired
    private SearchSuggestService searchSuggestService;
    @Autowired
    private SearchIndexService searchIndexService;

    @GetMapping("get")
    public Response get(String recipeCode){
        return Response.ok(searchInitService.get(recipeCode));
    }

    @GetMapping("search")
    public Response search(String keyword){
        return Response.ok(searchInitService.search(keyword));
    }

    @GetMapping("scroll_search")
    public Response scroll_search(String keyword){
        return Response.ok(searchInitService.searchScroll(keyword));
    }

    @GetMapping("multi_search")
    public Response multi_search(String keyword){
        return Response.ok(searchInitService.multiSearch(keyword));
    }

    @GetMapping("template_search")
    public Response template_search(String keyword){
        return Response.ok(searchInitService.templateSearch(keyword));
    }

    /**
     * init api
     */

    @PostMapping(RECIPE_INDEX_DATA)
    public Response index_data(String applianceType){
        searchIndexService.batchIndex(applianceType);
        return Response.ok();
    }

    @PostMapping(RECIPE_INDEX_RECREATE)
    public Response index_recreate(){
        return Response.ok(searchInitService.recreateIndex());
    }

    /**
     * search api
     */

    @GetMapping(RECIPE_SUGGEST)
    public Response suggest(String keyword){
        return Response.ok(searchSuggestService.suggest(keyword));
    }
}
