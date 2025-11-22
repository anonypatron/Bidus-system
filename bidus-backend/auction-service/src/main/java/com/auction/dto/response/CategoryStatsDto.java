package com.auction.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryStatsDto {

    private String categoryName;
    private Long count;

    @Builder
    public CategoryStatsDto(String categoryName, Long count) {
        this.categoryName = categoryName;
        this.count = count;
    }

}
