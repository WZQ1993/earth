package com.ziqingwang.feature.service;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.google.common.collect.Maps;
import com.ziqingwang.feature.config.AppConfig;
import com.ziqingwang.feature.entity.SearchSuggestVO;
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
	private static final String SUGGEST = "suggest_{filed}";
	private static final String FULL_PINYIN_SUGGEST = "full_pinyin_{filed}";
	private static final String FIRST_PINYIN_SUGGEST = "first_pinyin_{filed}";
	private static final String PINYIN_SUGGEST = "pinyin_{filed}";
	public static final Comparator<SearchSuggestVO> SUGGEST_COMPARATOR = Comparator.comparing(SearchSuggestVO::getScore).thenComparing(SearchSuggestVO::getSuggestText);

	/**
	 * 获取搜索建议，按以下顺序
	 * 1. 前缀建议
	 * 2. 全拼建议
	 * 3. 首字母建议
	 *
	 * @return
	 */
	public LinkedHashSet<SearchSuggestVO> suggest(String keyword, int size) {
		LinkedHashSet<SearchSuggestVO> suggestStr = Sets.newLinkedHashSet();
		Map<String, List<String>> type_suggest = completionSuggestion("recipe_name", keyword, size);
		if (!CollectionUtils.isEmpty(type_suggest)) {
			type_suggest.entrySet().stream().forEach(
					entry -> {
						String suggestType = entry.getKey();
						List<SearchSuggestVO> suggest = entry.getValue().stream()
								.map(v -> SearchSuggestVO.builder().suggestText(v).score(v.length()).build())
								.sorted(SUGGEST_COMPARATOR)
								.collect(Collectors.toList());
						log.warn("[搜索建议] 建议类型：{}，建议选项：{}", suggestType, suggest);
						suggestStr.addAll(suggest);
					}
			);
		}
		return suggestStr;
	}

	// 1. completionSuggestion 前缀建议
	public Map<String, List<String>> completionSuggestion(String filed, String prefix, int size) {
		Map<String, List<String>> suggest_result = Maps.newHashMap();
		SearchRequest request = new SearchRequest(appConfig.getRecipe_detail_index());
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		SuggestBuilder suggestBuilder = new SuggestBuilder();
		suggestBuilder.addSuggestion(SUGGEST.replace("{filed}", filed), SuggestBuilders.completionSuggestion("info.recipeName_suggest").text(prefix).size(size));
		suggestBuilder.addSuggestion(FULL_PINYIN_SUGGEST.replace("{filed}", filed), SuggestBuilders.completionSuggestion("info.recipeName_suggest.full_pinyin").text(prefix).size(size));
		suggestBuilder.addSuggestion(FIRST_PINYIN_SUGGEST.replace("{filed}", filed), SuggestBuilders.completionSuggestion("info.recipeName_suggest.first_pinyin").text(prefix).size(size));
		suggestBuilder.addSuggestion(PINYIN_SUGGEST.replace("{filed}", filed), SuggestBuilders.completionSuggestion("info.recipeName_suggest.pinyin").text(prefix).size(size));
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

	// 4. term suggest（用于词组纠错 - 英文）
	public Map<String, List<String>> termSuggestion(String keyword) {
		return null;
	}

	// 5. phase suggest（用于关联建议 - 英文）
	public Map<String, List<String>> phaseSuggestion(String keyword) {
		return null;
	}
}
