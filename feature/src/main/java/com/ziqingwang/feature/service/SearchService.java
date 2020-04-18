package com.ziqingwang.feature.service;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ziqingwang.feature.config.AppConfig;
import com.ziqingwang.feature.entity.RecipeIndexDTO;
import com.ziqingwang.feature.entity.RecipeSearchParamDTO;
import com.ziqingwang.feature.entity.SearchResultVO;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * @author: Ziven
 * @date: 2020/04/18
 **/
@Service
@Slf4j
public class SearchService {
	@Resource(name = "esClient")
	private RestHighLevelClient esClient;
	@Autowired
	private AppConfig appConfig;

	public SearchResultVO search(RecipeSearchParamDTO searchParam) {
		SearchResultVO<RecipeIndexDTO.RecipeInfoVO> result = SearchResultVO.empty();
		SearchRequest request = new SearchRequest(appConfig.getRecipe_detail_index());
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		// 查询（通过copy_to实现全文搜索）
		sourceBuilder.query(
				QueryBuilders.multiMatchQuery(searchParam.getKeyword(), "text", "text.pinyin")
		);
		// 定制结果
		sourceBuilder.fetchSource("info.*","");
		// 高亮结果
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		HighlightBuilder.Field highlightText = new HighlightBuilder.Field("text");
		highlightBuilder.field(highlightText);
		sourceBuilder.highlighter(highlightBuilder);
		request.source(sourceBuilder);
		try {
			SearchResponse resp = esClient.search(request, RequestOptions.DEFAULT);
			if(Objects.nonNull(resp)){
				long count = resp.getHits().getTotalHits().value;
				List<SearchResultVO.SearchDataVO<RecipeIndexDTO.RecipeInfoVO>> datas = Lists.newArrayList();
				if(count > 0){
					datas = Arrays.stream(resp.getHits().getHits())
							.map(hit->{
								JSONObject hitData = JSON.parseObject(hit.getSourceAsString(), JSONObject.class);
								RecipeIndexDTO.RecipeInfoVO data = hitData.getJSONObject("info").toJavaObject(RecipeIndexDTO.RecipeInfoVO.class);
								List<String> highlight = hit.getHighlightFields().entrySet().stream()
										.map(Map.Entry::getValue)
										.flatMap(highlightField -> Arrays.stream(highlightField.getFragments()))
										.map(text -> text.string())
										.collect(Collectors.toList());
								log.warn("[搜索结果] - 高亮：{}", highlight);
								return SearchResultVO.SearchDataVO.<RecipeIndexDTO.RecipeInfoVO>builder()
										.data(data)
										.score(hit.getScore())
										.highlighting(highlight.stream().sorted(Comparator.comparingInt(String::length).reversed()).findFirst().orElse(""))
										.build();
							})
							.collect(Collectors.toList());
				}
				result.setCount(count);
				result.setDatas(datas);
			}
			log.warn("[elasticSearch] data:{}", resp);
		} catch (Exception e) {
			log.error("[elasticSearch] - index error, msg:{}", e);
		}
		return result;
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
}
