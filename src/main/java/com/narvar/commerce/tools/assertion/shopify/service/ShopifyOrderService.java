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
import com.narvar.commerce.tools.assertion.shopify.domain.ShopifyOrder;
import com.narvar.commerce.tools.assertion.shopify.domain.ShopifyOrders;
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
public class ShopifyOrderService {
    private static final Logger LOG = LoggerFactory.getLogger(ShopifyOrderService.class);

    private HttpClient httpClient;

    private ObjectMapper objectMapper;

    @Autowired
    ShopifyOrderService() {
        this.httpClient = HttpClientBuilder.create().build();
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public List<ShopifyOrder> getOrders(String storeName, String accessToken, LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        ShopifyOrder firstOrder = getFirstOrder(storeName, accessToken, startDate, endDate);
        ShopifyOrder lastOrder = getLastOrder(storeName, accessToken, startDate, endDate);
        Long sinceId = firstOrder.getId();
        Long endId = lastOrder.getId();
        Long currentStartIndex = sinceId;
        List<ShopifyOrder> shopifyOrderList = new ArrayList<>();
        while (currentStartIndex <= endId) {
            String uri = ShopifyUrl.builder()
                    .urlPath(ShopifyEndpoints.ORDER.API_ROOT_JSON)
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
            ShopifyOrders shopifyOrders = objectMapper.readValue(response.getEntity().getContent(), ShopifyOrders.class);
            shopifyOrderList.addAll(shopifyOrders.shopifyOrderList);
            LOG.info("Total Number of Orders Fetched : " + shopifyOrderList.size());
            if (shopifyOrders.shopifyOrderList.size() > 0) {
                currentStartIndex = shopifyOrders.shopifyOrderList.get(shopifyOrders.shopifyOrderList.size() - 1).getId();
            } else {
                currentStartIndex += 250;
            }
            TimeUnit.MILLISECONDS.sleep(500);
        }
        return shopifyOrderList;
    }

    public ShopifyOrder getFirstOrder(String storeName, String accessToken, LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        String uri = ShopifyUrl.builder()
                .urlPath(ShopifyEndpoints.ORDER.API_ROOT_JSON)
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
        ShopifyOrders shopifyOrders = objectMapper.readValue(response.getEntity().getContent(), ShopifyOrders.class);
        if (shopifyOrders.getShopifyOrderList().size() > 0) {
            return shopifyOrders.getShopifyOrderList().get(0);
        }
        return null;
    }

    public ShopifyOrder getLastOrder(String storeName, String accessToken, LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        String uri = ShopifyUrl.builder()
                .urlPath(ShopifyEndpoints.ORDER.API_ROOT_JSON)
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
        ShopifyOrders shopifyOrders = objectMapper.readValue(response.getEntity().getContent(), ShopifyOrders.class);
        if (shopifyOrders.getShopifyOrderList().size() > 0) {
            return shopifyOrders.getShopifyOrderList().get(0);
        }
        return null;
    }


}
