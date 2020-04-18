package com.ziqingwang.feature.entity;

import java.util.Comparator;

import lombok.*;

/**
 * @author: Ziven
 * @date: 2020/04/17
 **/
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SearchSuggestVO {
	private String suggestText;
	private int score;
}
