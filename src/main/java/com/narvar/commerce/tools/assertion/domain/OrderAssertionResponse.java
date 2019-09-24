/*
 *
 * Copyright (c) 2019 Narvar Inc.
 * All rights reserved
 *
 */
package com.narvar.commerce.tools.assertion.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderAssertionResponse {
    private boolean isSuccess;
    private String message;
    private List<String> validatedOrders;
    private List<String> invalidOrders;
}
