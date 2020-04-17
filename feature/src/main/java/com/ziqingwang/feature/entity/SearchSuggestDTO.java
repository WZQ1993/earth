package com.ziqingwang.feature.entity;

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
public class SearchSuggestDTO implements Comparable{
	private String suggestText;
	private int score;

	@Override
	public int compareTo(Object o) {
		if(o instanceof SearchSuggestDTO){
			return score - ((SearchSuggestDTO)o).getScore();
		}else {
			return 1;
		}
	}
}
