package com.narvar.commerce.tools.assertion.service;

import com.narvar.commerce.tools.assertion.domain.ProductAssertionRequest;
import com.narvar.commerce.tools.assertion.domain.ProductAssertionResponse;
import com.narvar.commerce.tools.assertion.product.ProductTableReadService;
import com.narvar.commerce.tools.assertion.shopify.domain.ShopifyProduct;
import com.narvar.commerce.tools.assertion.shopify.service.ShopifyProductService;
import com.narvar.commerce.tools.assertion.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {

    private ShopifyProductService shopifyProductService;

    private ProductTableReadService productTableReadService;

    @Autowired
    public ProductService(ShopifyProductService shopifyProductService, ProductTableReadService productTableReadService) {
        this.shopifyProductService = shopifyProductService;
        this.productTableReadService = productTableReadService;
    }

    public ProductAssertionResponse assertProductData(ProductAssertionRequest productAssertionRequest) {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.DATE, (-1) * (60));
        Date startTime = calendar.getTime();

        if (Objects.nonNull(productAssertionRequest.getStartTime()) && Objects.nonNull(productAssertionRequest.getEndTime())
                && productAssertionRequest.getEndTime().after(productAssertionRequest.getStartTime())) {
            startTime = productAssertionRequest.getStartTime();
            endTime = productAssertionRequest.getEndTime();
        }

        try {
            List<ShopifyProduct> shopifyProductList = shopifyProductService.getProducts(productAssertionRequest.getStoreName(),
                    productAssertionRequest.getAccessToken(), DateUtils.getLocalDateTime(startTime),
                    DateUtils.getLocalDateTime(endTime));
            return productTableReadService.validateProducts(productAssertionRequest.getStoreName(), shopifyProductList);
        } catch (Exception e) {
            return new ProductAssertionResponse(false, e.getMessage(), null, null, null, null, null, null, null, null, null, null);
        }
    }
}
