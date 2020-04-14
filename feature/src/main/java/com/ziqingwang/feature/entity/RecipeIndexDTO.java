package com.ziqingwang.feature.entity;

import lombok.*;

import java.util.List;
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIndexDTO {
    /**
     * 食谱基本数据
     */
    @Builder.Default
    private String type = "recipeDetail";
    private String _all;
    private Integer recipeCode;
    private RecipeInfoVO info;
    private RecipeTagVO tag;
    private List<RecipeFoodVO> foods;
//    private List<NutritionsVO> nutritions;
    private List<RecipeStepVO> keySteps;

    private RecipeUserVO user;

    @Data
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeInfoVO {
        private String recipeName;
        private String recipeCate;
        private Integer recipeType;
        private String description;
        private String tips;
        private String difficulty;
//        private int difficultyValue;
        private Float costTimeMin;
        private Integer person;
//        private Double energy;
        /** 资源 */
//        private String thumbnailUrl11;
//        private String thumbnailUrl43;
//        private String thumbnailUrl169;
//        private String photoUrl;
//        private String photoUrl43;
//        private String photoUrl11;
//        private String videoUrl;
//        private String videoUrl43;
//        private String videoUrl916;
        private Long updateTime;
        /**
         * 事业部特有的数据
         */
        /** 烹饪曲线 */
//        private String curveUrl;
        /** 是否支持快捷烹饪模式*/
        private Boolean enableQuickCooking;
        /** 食谱预处理 */
        private String prepareTips;
        /** 热数据 */
        private int likeNum;
        private int collectionNum;
    }

    @Data
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeTagVO {
        private String cuisineNames;
        private String cookwayNames;
        private String tasteNames;
        private String dishesNames;
        private String seasonNames;
        private String foodNames;
        private String difficultyNames;
        private String functionNames;
        private String suitCrowdNames;
        private String tabooCrowdNames;
    }

    @Data
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeFoodVO {
        private String foodName;
        private Integer foodType;
        private String foodCode;
    }

    @Data
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeStepVO {
        private String description;
    }

    @Data
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeUserVO {
        private String nickName;
        private String profileUrl;
        private String userId;
        private String signature;
        private String introduction;
    }
}
