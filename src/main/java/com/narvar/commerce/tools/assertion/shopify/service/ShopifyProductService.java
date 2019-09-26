/*
 *
 * Copyright (c) 2019 Narvar Inc.
 * All rights reserved
 *
 */
package com.narvar.commerce.tools.assertion.shopify.service;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.narvar.commerce.tools.assertion.shopify.constants.ShopifyEndpoints;
import com.narvar.commerce.tools.assertion.shopify.constants.ShopifyHeaders;
import com.narvar.commerce.tools.assertion.shopify.constants.ShopifyRequestConstants;
import com.narvar.commerce.tools.assertion.shopify.domain.ShopifyProduct;
import com.narvar.commerce.tools.assertion.shopify.domain.ShopifyProducts;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ShopifyProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ShopifyProductService.class);

    private HttpClient httpClient;

    private ObjectMapper objectMapper;

    @Autowired
    ShopifyProductService() {
        this.httpClient = HttpClientBuilder.create().build();
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public List<ShopifyProduct> getProducts(String storeName, String accessToken, LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        ShopifyProduct firstProduct = getFirstProduct(storeName, accessToken, startDate, endDate);
        ShopifyProduct lastProduct = getLastProduct(storeName, accessToken, startDate, endDate);
        Long sinceId = firstProduct.getId() - 1L;
        Long endId = lastProduct.getId();
        Long currentStartIndex = sinceId;
        List<ShopifyProduct> shopifyProductList = new ArrayList<>();
        while (currentStartIndex <= endId) {
            String uri = ShopifyUrl.builder()
                    .urlPath(ShopifyEndpoints.PRODUCT.API_ROOT_JSON)
                    .updateAtMin(startDate)
                    .updateAtMax(endDate)
                    .sortOrderKey(ShopifyRequestConstants.SortOrderKey.CREATED_AT)
                    .sortOrder(ShopifyRequestConstants.SortOrder.ASC)
                    .sinceId(currentStartIndex)
                    .limit(250)
                    .status(ShopifyRequestConstants.Status.ANY)
                    .build();
            HttpGet request = new HttpGet("https://" + storeName + uri);
            request.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            request.addHeader(ShopifyHeaders.ACCESS_TOKEN, accessToken);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed with HTTP error code : " + response.getStatusLine().getStatusCode());
            }
            ShopifyProducts shopifyProducts = objectMapper.readValue(response.getEntity().getContent(), ShopifyProducts.class);
            shopifyProductList.addAll(shopifyProducts.getShopifyProductList());
            LOG.info("Total Number of Products Fetched : " + shopifyProductList.size());
            if (shopifyProducts.getShopifyProductList().size() > 0) {
                currentStartIndex = shopifyProducts.getShopifyProductList().get(shopifyProducts.getShopifyProductList().size() - 1).getId();
            } else {
                currentStartIndex += 250;
            }
            TimeUnit.MILLISECONDS.sleep(500);
        }
        return shopifyProductList;
    }

    public ShopifyProduct getFirstProduct(String storeName, String accessToken, LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        String uri = ShopifyUrl.builder()
                .urlPath(ShopifyEndpoints.PRODUCT.API_ROOT_JSON)
                .limit(1)
                .updateAtMin(startDate)
                .updateAtMax(endDate)
                .sortOrderKey(ShopifyRequestConstants.SortOrderKey.CREATED_AT)
                .sortOrder(ShopifyRequestConstants.SortOrder.ASC)
                .status(ShopifyRequestConstants.Status.ANY)
                .build();
        HttpGet request = new HttpGet("https://" + storeName + uri);
        request.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        request.addHeader(ShopifyHeaders.ACCESS_TOKEN, accessToken);
        HttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed with HTTP error code : " + response.getStatusLine().getStatusCode());
        }
        ShopifyProducts shopifyProducts = objectMapper.readValue(response.getEntity().getContent(), ShopifyProducts.class);
        if (shopifyProducts.getShopifyProductList().size() > 0) {
            return shopifyProducts.getShopifyProductList().get(0);
        }
        return null;
    }

    public ShopifyProduct getLastProduct(String storeName, String accessToken, LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        String uri = ShopifyUrl.builder()
                .urlPath(ShopifyEndpoints.PRODUCT.API_ROOT_JSON)
                .limit(1)
                .updateAtMin(startDate)
                .updateAtMax(endDate)
                .sortOrderKey(ShopifyRequestConstants.SortOrderKey.CREATED_AT)
                .sortOrder(ShopifyRequestConstants.SortOrder.DESC)
                .status(ShopifyRequestConstants.Status.ANY)
                .build();
        HttpGet request = new HttpGet("https://" + storeName + uri);
        request.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        request.addHeader(ShopifyHeaders.ACCESS_TOKEN, accessToken);
        HttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed with HTTP error code : " + response.getStatusLine().getStatusCode());
        }
        ShopifyProducts shopifyProducts = objectMapper.readValue(response.getEntity().getContent(), ShopifyProducts.class);
        if (shopifyProducts.getShopifyProductList().size() > 0) {
            return shopifyProducts.getShopifyProductList().get(0);
        }
        return null;
    }
}
