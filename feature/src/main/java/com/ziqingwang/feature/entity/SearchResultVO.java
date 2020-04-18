package com.ziqingwang.feature.entity;

import java.util.List;

import lombok.*;

/**
 * @author: Ziven
 * @date: 2020/04/18
 **/
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultVO<T>{
	private List<SearchDataVO<T>> datas;
	private long count;

	public static <T> SearchResultVO empty(){
		return new SearchResultVO<T>();
	}
	@Data
	@ToString
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static final class SearchDataVO<T>{
		private T data;
		private String highlighting;
		private double score;
	}
}
