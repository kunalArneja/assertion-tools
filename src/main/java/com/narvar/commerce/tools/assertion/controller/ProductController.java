/*
 *
 * Copyright (c) 2019 Narvar Inc.
 * All rights reserved
 *
 */
package com.narvar.commerce.tools.assertion.controller;

import com.narvar.commerce.tools.assertion.domain.ProductAssertionRequest;
import com.narvar.commerce.tools.assertion.domain.ProductAssertionResponse;
import com.narvar.commerce.tools.assertion.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller to handle request for asserting product data.
 */
@RestController
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(path = "/assert/product", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ProductAssertionResponse process(@RequestBody ProductAssertionRequest productAssertionRequest) {
        LOGGER.debug("Received request for storeName = {}", productAssertionRequest.getStoreName());
        return productService.assertProductData(productAssertionRequest);
    }
}
