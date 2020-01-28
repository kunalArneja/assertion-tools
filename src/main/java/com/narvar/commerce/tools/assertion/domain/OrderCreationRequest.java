package com.narvar.commerce.tools.assertion.domain;

import lombok.Data;

@Data
public class OrderCreationRequest {
    Integer numberOfOrders;
    String url;
    String token;
    String payload;
}
