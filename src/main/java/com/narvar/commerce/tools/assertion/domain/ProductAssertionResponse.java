package com.narvar.commerce.tools.assertion.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAssertionResponse {
    private boolean isSuccess;
    private String message;
    private List<Long> validatedProductIds;
    private List<Long> invalidProductIds;
    private List<Long> validatedVariantIds;
    private List<Long> invalidVariantIds;
    private Integer validatedProductIdsCount;
    private Integer invalidProductIdsCount;
    private Integer validatedVariantIdsCount;
    private Integer invalidVariantIdsCount;
    private Integer expectedCount;
    private Integer fetchedCount;

}
