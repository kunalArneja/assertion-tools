/*
 *
 * Copyright (c) 2019 Narvar Inc.
 * All rights reserved
 *
 */

package com.narvar.commerce.tools.assertion.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.util.Date;

@Data
@NoArgsConstructor
@ToString
public class OrderAssertionRequest {

    @NotBlank
    private String accountId;

    @NotBlank
    private String storeName;

    @NotBlank
    private String authToken;

    @NotBlank
    private String accessToken;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZZZ")
    @Past
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZZZ")
    @Past
    private Date endTime;

    @NotBlank
    private Boolean checkAllOrders;
}
