/*
 *
 * Copyright (c) 2019 Narvar Inc.
 * All rights reserved
 *
 */
package com.narvar.commerce.tools.assertion.controller;

import com.narvar.commerce.tools.assertion.domain.OrderAssertionRequest;
import com.narvar.commerce.tools.assertion.domain.OrderAssertionResponse;
import com.narvar.commerce.tools.assertion.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller to handle request for asserting order data.
 */
@RestController
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(path = "/assert/order", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public OrderAssertionResponse process(@RequestBody OrderAssertionRequest orderAssertionRequest) {
        LOGGER.debug("Received request for retailer = {}", orderAssertionRequest.getAccountId());
        return orderService.assertOrderData(orderAssertionRequest);
    }
}
