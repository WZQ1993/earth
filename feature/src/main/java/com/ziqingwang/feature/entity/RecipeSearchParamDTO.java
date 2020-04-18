package com.ziqingwang.feature.entity;

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
public class RecipeSearchParamDTO {
	private String keyword;
}
