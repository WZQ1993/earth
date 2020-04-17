package com.ziqingwang.feature.service;

import java.time.Instant;
import java.util.*;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ziqingwang.feature.config.AppConfig;
import com.ziqingwang.feature.entity.RecipeIndexDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.AcknowledgedResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.DeleteAliasRequest;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

@Service
@Slf4j
public class SearchInitService {

	@Resource(name = "esClient")
	private RestHighLevelClient esClient;
	@Autowired
	private RecipeDataService recipeDataService;
	@Autowired
	private AppConfig appConfig;

	// todo 2. highlighting 高亮
	// todo 3. provide full-text-search using mapping api(set "copy_to" field)
	// todo 5. trying template index (pattern)
	// todo 6. using pipeline to preprocess data before index
	// todo 7. try using script
	// todo 8. enrich data during index data

	public Object recreateIndex() {
		CreateIndexResponse response = null;
		try {
			String indexName = "recipe_detail_" + Instant.now().getEpochSecond();
			CreateIndexRequest request = new CreateIndexRequest(indexName);
			// index setting
			String recipe_detail_setting_json = FileUtils.readFileToString(appConfig.getRecipe_detail_setting_res().getFile(), "UTF-8");
			request.settings(recipe_detail_setting_json, XContentType.JSON);
			// index mapping
			String recipe_detail_mapping_json = FileUtils.readFileToString(appConfig.getRecipe_detail_mapping_res().getFile(), "UTF-8");
			request.mapping(recipe_detail_mapping_json, XContentType.JSON);
			// index alias
			request.alias(new Alias(appConfig.getRecipe_detail_index()));
			// execution
			response = esClient.indices().create(request, RequestOptions.DEFAULT);
			// index data
			Map<String, Set<AliasMetaData>> aliasMap = getAliases(appConfig.getRecipe_detail_index());
			if (!CollectionUtils.isEmpty(aliasMap)) {
				aliasMap.entrySet().stream().forEach(alias -> {
					if (!indexName.equals(alias.getKey())) {
						try {
							deleteIndexAlias(alias.getKey(), appConfig.getRecipe_detail_index());
						} catch (Exception e) {
							log.warn("[deleteIndex] error:{}", e);
						}
						log.warn("[deleteIndex] alias:{}, index:{}", appConfig.getRecipe_detail_index(), alias.getKey());
					}
				});
			}
		} catch (Exception e) {
			log.error("[createIndex] error, msg:{}", e);
		}
		return response;
	}

	private Map<String, Set<AliasMetaData>> getAliases(String indexAlias) throws Exception {
		GetAliasesRequest request = new GetAliasesRequest(indexAlias);
		GetAliasesResponse response = esClient.indices().getAlias(request, RequestOptions.DEFAULT);
		if (Objects.isNull(response)) {
			log.error("[getAliases] response null");
		}
		return response.getAliases();
	}

	private boolean deleteIndexAlias(String index, String alias) throws Exception {
		DeleteAliasRequest request = new DeleteAliasRequest(index, alias);
		AcknowledgedResponse response = esClient.indices().deleteAlias(request, RequestOptions.DEFAULT);
		if (Objects.isNull(response)) {
			log.error("[deleteIndexAlias] response null");
		}
		return response.isAcknowledged();
	}

	public GetResponse get(String recipeCode) {
		GetResponse resp = null;
		try {
			GetRequest getRequest = new GetRequest(appConfig.getRecipe_detail_index(), recipeCode);
			resp = esClient.get(getRequest, RequestOptions.DEFAULT);
			log.warn("[get] data:{}", resp);
		} catch (Exception e) {
			log.error("[elasticSearch] - index error, msg:{}", e);
		}
		return resp;
	}

	public SearchResponse search(String keyword) {
		SearchRequest request = new SearchRequest(appConfig.getRecipe_detail_index());
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.termQuery("info.recipeName", keyword));
		SearchResponse resp = null;
		try {
			request.source(sourceBuilder);
			resp = esClient.search(request, RequestOptions.DEFAULT);
			log.warn("[query] data:{}", resp);
		} catch (Exception e) {
			log.error("[elasticSearch] - index error, msg:{}", e);
		}
		return resp;
	}

	public Object searchScroll(String keyword) {
		// set scroll search
		final Scroll scroll = new Scroll(TimeValue.MINUS_ONE);
		SearchRequest request = new SearchRequest("recipe");
		request.scroll(scroll);
		// generate search source
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(termQuery("dataSource", "1"));
		searchSourceBuilder.size(50);
		request.source(searchSourceBuilder);
		try {
			// post search
			SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
			// get scroll id
			String scrollId = response.getScrollId();
			SearchHit[] hits = response.getHits().getHits();
			log.warn("[elasticSearch] - scroll search data:{}", hits);
			// cycle to get data
			while (Objects.nonNull(hits) && hits.length > 0) {
				SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
				scrollRequest.scroll(scroll);
				SearchResponse scrollResponse = esClient.scroll(scrollRequest, RequestOptions.DEFAULT);
				scrollId = scrollResponse.getScrollId();
				hits = scrollResponse.getHits().getHits();
				log.warn("[elasticSearch] - scroll search data:{}", hits);
			}
			// clear scroll context
			ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
			clearScrollRequest.addScrollId(scrollId);
			ClearScrollResponse clearScrollResponse = esClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
			boolean succeeded = clearScrollResponse.isSucceeded();
			if (!succeeded) {
				log.error("[elasticSearch] - scroll search can not clear scroll context");
			}
		} catch (Exception e) {
			log.error("[elasticSearch] - scroll search error, msg:{}", e);
		}
		return null;
	}

	public Object multiSearch(String keyword) {
		MultiSearchRequest request = new MultiSearchRequest();
		SearchRequest request_1 = new SearchRequest("recipe");
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchQuery("foods", keyword));
		request_1.source(sourceBuilder);
		request.add(request_1);
		SearchRequest request_2 = new SearchRequest("recipe");
		sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchQuery("description", keyword));
		request_2.source(sourceBuilder);
		request.add(request_2);

		MultiSearchResponse response;
		try {
			response = esClient.msearch(request, RequestOptions.DEFAULT);
			MultiSearchResponse.Item responce_1 = response.getResponses()[0];
			MultiSearchResponse.Item responce_2 = response.getResponses()[1];
			log.warn("[elasticSearch] multiSearch resp:{}, {}", responce_1, responce_2);
		} catch (Exception e) {
			log.error("[elasticSearch] multiSearch error, msg:{}", e);
		}
		return null;
	}

	public Object templateSearch(String keyword) {
		// 1. register script not yet available in high level client
		// ........
		// 2. inline template
		SearchTemplateRequest request = new SearchTemplateRequest();
		request.setRequest(new SearchRequest("recipe"));
		request.setScriptType(ScriptType.INLINE);
		request.setScript(
				"{" +
						"  \"query\": { \"match\" : { \"{{field}}\" : \"{{value}}\" } }," +
						"  \"size\" : \"{{size}}\"" +
						"}");

		Map<String, Object> scriptParams = new HashMap<>();
		scriptParams.put("field", "info.recipeName");
		scriptParams.put("value", keyword);
		scriptParams.put("size", 5);
		request.setScriptParams(scriptParams);
		SearchTemplateResponse response = null;
		try {
			response = esClient.searchTemplate(request, RequestOptions.DEFAULT);
		} catch (Exception e) {
			log.error("[elasticSearch] multiSearch error, msg:{}", e);
		}
		return response;
	}

	public Object deleteIndex(String index) {
		DeleteIndexRequest request = new DeleteIndexRequest(index);
		org.elasticsearch.action.support.master.AcknowledgedResponse response = null;
		try {
			response = esClient.indices().delete(request, RequestOptions.DEFAULT);
		} catch (Exception exception) {
			if (exception instanceof ElasticsearchException && ((ElasticsearchException) exception).status() == RestStatus.NOT_FOUND) {
				log.error("[deleteIndex] index not exists, msg:{}", exception);
			} else {
				log.error("[deleteIndex] error msg:{}", exception);
			}
		}
		return response;
	}
}
