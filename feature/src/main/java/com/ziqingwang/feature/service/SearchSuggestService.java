package com.ziqingwang.feature.service;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.google.common.collect.Maps;
import com.ziqingwang.feature.config.AppConfig;
import com.ziqingwang.feature.entity.SearchSuggestDTO;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 搜索建议
 * 1. 前缀
 * 2. 短语
 * 3. 纠错
 *
 * @author: Ziven
 * @date: 2020/04/17
 **/
@Service
@Slf4j
public class SearchSuggestService {
	@Autowired
	private AppConfig appConfig;
	@Resource(name = "esClient")
	private RestHighLevelClient esClient;

	public Set<SearchSuggestDTO> suggest(String keyword) {
		TreeSet<SearchSuggestDTO> suggestStr = Sets.newTreeSet();
		Map<String, List<String>> completionSuggestions = completionSuggestion(keyword);
		if (!CollectionUtils.isEmpty(completionSuggestions)) {
			completionSuggestions.entrySet().stream().forEach(
					entry -> suggestStr.addAll(
							entry.getValue().stream()
									.map(v ->
											SearchSuggestDTO.builder().suggestText(v).score(v.length()).build()
									)
									.collect(Collectors.toSet())
					)
			);
		}
		return suggestStr;
	}

	// 1. completionSuggestion 前缀建议
	public Map<String, List<String>> completionSuggestion(String prefix) {
		Map<String, List<String>> suggest_result = Maps.newHashMap();
		SearchRequest request = new SearchRequest(appConfig.getRecipe_detail_index());
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		SuggestBuilder suggestBuilder = new SuggestBuilder();
		suggestBuilder.addSuggestion("suggest_recipe_name", SuggestBuilders.completionSuggestion("info.recipeName_suggest").text(prefix));
		suggestBuilder.addSuggestion("suggest_food_name", SuggestBuilders.completionSuggestion("foods.foodName_suggest").text(prefix));
		sourceBuilder.suggest(suggestBuilder);
		sourceBuilder.fetchSource("", "_source");
		request.source(sourceBuilder);
		SearchResponse resp = null;
		try {
			resp = esClient.search(request, RequestOptions.DEFAULT);
			if (Objects.nonNull(resp) && Objects.nonNull(resp.getSuggest())) {
				Suggest suggestions = resp.getSuggest();
				suggestions.forEach(suggest -> {
					CompletionSuggestion c_suggest = (CompletionSuggestion) suggest;
					String suggestName = c_suggest.getName();
					c_suggest.getEntries().stream()
							.forEach(
									entry -> {
										int length = entry.getLength();
										log.warn("【搜索建议】长度：{}", length);
										List<String> optionStrList = suggest_result.getOrDefault(suggestName, Lists.newArrayList());
										optionStrList.addAll(
												entry.getOptions().stream().map(op -> op.getText().string()).collect(Collectors.toList())
										);
										suggest_result.put(suggestName, optionStrList);
									}
							);
				});
			}
			log.warn("[query] data:{}", resp);
		} catch (Exception e) {
			log.error("[elasticSearch] - index error, msg:{}", e);
		}
		return suggest_result;
	}
}
