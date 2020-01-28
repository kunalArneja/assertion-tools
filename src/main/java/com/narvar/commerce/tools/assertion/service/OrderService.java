package com.narvar.commerce.tools.assertion.service;

import com.narvar.commerce.tools.assertion.domain.OrderAssertionRequest;
import com.narvar.commerce.tools.assertion.domain.OrderAssertionResponse;
import com.narvar.commerce.tools.assertion.domain.OrderCreationRequest;
import com.narvar.commerce.tools.assertion.magento.MagentoApiService;
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

    private MagentoApiService magentoApiService;

    private OrderApiService orderApiService;

    @Autowired
    public OrderService(ShopifyOrderService shopifyOrderService, OrderApiService orderApiService, MagentoApiService magentoApiService) {
        this.shopifyOrderService = shopifyOrderService;
        this.orderApiService = orderApiService;
        this.magentoApiService = magentoApiService;
    }

    public OrderAssertionResponse assertShopifyOrderData(OrderAssertionRequest orderAssertionRequest) {
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
            return orderApiService.validateShopifyOrders(shopifyOrderList, orderAssertionRequest.getAuthToken(), orderAssertionRequest.getAccountId(), orderAssertionRequest.getCheckAllOrders());
        } catch (Exception e) {
            return new OrderAssertionResponse(false, e.getMessage(), null, null, null, null);
        }
    }

    public OrderAssertionResponse assertOrderData(OrderAssertionRequest orderAssertionRequest) {

        try {
            return orderApiService.validateOrders(orderAssertionRequest.getStartId(),
                    orderAssertionRequest.getEndId(),
                    orderAssertionRequest.getAuthToken(),
                    orderAssertionRequest.getAccountId(),
                    orderAssertionRequest.getCheckAllOrders());
        } catch (Exception e) {
            return new OrderAssertionResponse(false, e.getMessage(), null, null, null, null);
        }
    }

    public List<String> createOrders(OrderCreationRequest orderCreationRequest) {

        try {
            return magentoApiService.createOrders(orderCreationRequest.getUrl(),
                    orderCreationRequest.getToken(), orderCreationRequest.getPayload(), orderCreationRequest.getNumberOfOrders());
        } catch (Exception e) {
            return null;
        }
    }
}
