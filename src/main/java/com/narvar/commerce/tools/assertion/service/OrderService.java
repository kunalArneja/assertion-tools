package com.narvar.commerce.tools.assertion.service;

import com.narvar.commerce.tools.assertion.domain.OrderAssertionRequest;
import com.narvar.commerce.tools.assertion.domain.OrderAssertionResponse;
import com.narvar.commerce.tools.assertion.orderapi.OrderApiService;
import com.narvar.commerce.tools.assertion.shopify.domain.ShopifyOrder;
import com.narvar.commerce.tools.assertion.shopify.service.ShopifyOrderService;
import com.narvar.commerce.tools.assertion.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {

    private ShopifyOrderService shopifyOrderService;

    private OrderApiService orderApiService;

    @Autowired
    public OrderService(ShopifyOrderService shopifyOrderService, OrderApiService orderApiService) {
        this.shopifyOrderService = shopifyOrderService;
        this.orderApiService = orderApiService;
    }

    public OrderAssertionResponse assertOrderData(OrderAssertionRequest orderAssertionRequest) {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.DATE, (-1) * (60));
        Date startTime = calendar.getTime();

        if (Objects.nonNull(orderAssertionRequest.getStartTime()) && Objects.nonNull(orderAssertionRequest.getEndTime())
                && orderAssertionRequest.getEndTime().after(orderAssertionRequest.getStartTime())) {
            startTime = orderAssertionRequest.getStartTime();
            endTime = orderAssertionRequest.getEndTime();
        }

        try {
            List<ShopifyOrder> shopifyOrderList = shopifyOrderService.getOrders(orderAssertionRequest.getStoreName(),
                    orderAssertionRequest.getAccessToken(), DateUtils.getLocalDateTime(startTime),
                    DateUtils.getLocalDateTime(endTime));
            return orderApiService.validateOrders(shopifyOrderList, orderAssertionRequest.getAuthToken(), orderAssertionRequest.getAccountId(), orderAssertionRequest.getCheckAllOrders());
        } catch (Exception e) {
            return new OrderAssertionResponse(false, e.getMessage(), null, null);
        }
    }
}
