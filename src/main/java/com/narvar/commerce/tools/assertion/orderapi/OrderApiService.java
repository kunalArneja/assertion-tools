/*
 *
 * Copyright (c) 2019 Narvar Inc.
 * All rights reserved
 *
 */

package com.narvar.commerce.tools.assertion.orderapi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.narvar.commerce.tools.assertion.domain.OrderAssertionResponse;
import com.narvar.commerce.tools.assertion.orderapi.domain.Order;
import com.narvar.commerce.tools.assertion.shopify.domain.ShopifyOrder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderApiService {
    private static final Logger LOG = LoggerFactory.getLogger(OrderApiService.class);

    private HttpClient httpClient;

    private ObjectMapper objectMapper;

    @Autowired
    OrderApiService() {
        this.httpClient = HttpClientBuilder.create().build();
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public OrderAssertionResponse validateShopifyOrders(List<ShopifyOrder> shopifyOrders, String authToken, String retailerId, Boolean checkAllOrders) throws Exception {
        OrderAssertionResponse orderAssertionResponse = new OrderAssertionResponse();
        List<String> validatedOrders = new ArrayList<>();
        List<String> invalidOrders = new ArrayList<>();
        List<ShopifyOrder> ordersToCheck = new ArrayList<>();
        if (checkAllOrders) {
            ordersToCheck = shopifyOrders;
        } else {
            Set<Integer> idsToCheck = getFibonacciSeries(shopifyOrders.size());
            for (Integer index : idsToCheck) {
                ordersToCheck.add(shopifyOrders.get(index));
            }
        }
        for (ShopifyOrder shopifyOrder : ordersToCheck) {
            String orderNumber = Integer.toString(shopifyOrder.getOrderNumber());
            LOG.info("Trying to validate orderNumber : " + orderNumber);
            Boolean isValidOrderId = validateWithOrderApi(retailerId, authToken, orderNumber);
            if (isValidOrderId) {
                validatedOrders.add(orderNumber);
            } else {
                invalidOrders.add(orderNumber);
            }
        }
        if (CollectionUtils.isEmpty(invalidOrders)) {
            orderAssertionResponse.setSuccess(true);
        } else {
            orderAssertionResponse.setSuccess(false);
        }
        orderAssertionResponse.setInvalidOrders(invalidOrders);
        orderAssertionResponse.setValidatedOrders(validatedOrders);
        orderAssertionResponse.setInvalidOrdersCount(invalidOrders.size());
        orderAssertionResponse.setValidatedOrdersCount(validatedOrders.size());
        return orderAssertionResponse;

    }

    public OrderAssertionResponse validateOrders(String startId, String endId, String authToken, String retailerId, Boolean checkAllOrders) throws Exception {
        OrderAssertionResponse orderAssertionResponse = new OrderAssertionResponse();
        List<String> orderNumbers = new ArrayList<>();
        Integer start = Integer.parseInt(startId);
        Integer end = Integer.parseInt(endId);
        Integer length = startId.length();
        for (int i=start; i<end; i++){
            Integer currentNumber = i;
            orderNumbers.add(StringUtils.leftPad(currentNumber.toString(), length, '0'));
        }

        List<String> validatedOrders = new ArrayList<>();
        List<String> invalidOrders = new ArrayList<>();
        List<String> ordersToCheck = new ArrayList<>();
        if (checkAllOrders) {
            ordersToCheck = orderNumbers;
        } else {
            Set<Integer> idsToCheck = getFibonacciSeries(orderNumbers.size());
            for (Integer index : idsToCheck) {
                ordersToCheck.add(orderNumbers.get(index));
            }
        }
        for (String orderNumber : ordersToCheck) {
            LOG.info("Trying to validate orderNumber : " + orderNumber);
            Boolean isValidOrderId = validateWithOrderApi(retailerId, authToken, orderNumber);
            if (isValidOrderId) {
                validatedOrders.add(orderNumber);
            } else {
                invalidOrders.add(orderNumber);
            }
        }
        if (CollectionUtils.isEmpty(invalidOrders)) {
            orderAssertionResponse.setSuccess(true);
        } else {
            orderAssertionResponse.setSuccess(false);
        }
        orderAssertionResponse.setInvalidOrders(invalidOrders);
        orderAssertionResponse.setValidatedOrders(validatedOrders);
        orderAssertionResponse.setInvalidOrdersCount(invalidOrders.size());
        orderAssertionResponse.setValidatedOrdersCount(validatedOrders.size());
        return orderAssertionResponse;

    }

    private Boolean validateWithOrderApi(String retailerId, String authToken, String orderNumber) throws Exception {
        HttpGet request = new HttpGet("https://ws-qa01.narvar.qa/api/v1/orders/" + orderNumber);
        String auth = retailerId + ":" + authToken;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        request.addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpResponse response = httpClient.execute(request);
        Order order = objectMapper.readValue(response.getEntity().getContent(), Order.class);
        if (order.getStatus().equalsIgnoreCase("success")) {
            return true;
        }
        return false;
    }


    private Set<Integer> getFibonacciSeries(Integer limit) {
        Set<Integer> fibonacciSeries = new HashSet<>();
        fibonacciSeries.add(1);
        fibonacciSeries.add(2);
        fibonacciSeries.add(limit - 1);
        fibonacciSeries.add(limit - 2);
        Integer previousNumber = 1;
        Integer currentNumber = 2;
        while (currentNumber + previousNumber < limit) {
            Integer temp = previousNumber;
            previousNumber = currentNumber;
            currentNumber = currentNumber + temp;
            fibonacciSeries.add(currentNumber);
            fibonacciSeries.add(limit - currentNumber);
            if (currentNumber < limit / 2) {
                fibonacciSeries.add(limit / 2 - currentNumber);
                fibonacciSeries.add(limit / 2 + currentNumber);
            }
        }
        return fibonacciSeries;
    }

}
