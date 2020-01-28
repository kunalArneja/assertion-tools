package com.narvar.commerce.tools.assertion.magento;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.narvar.commerce.tools.assertion.shopify.constants.Headers;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MagentoApiService {
    private static final Logger LOG = LoggerFactory.getLogger(MagentoApiService.class);

    private HttpClient httpClient;

    private ObjectMapper objectMapper;

    @Autowired
    MagentoApiService() {
        this.httpClient = HttpClientBuilder.create().build();
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public List<String> createOrders(String url, String accessToken, String payload, Integer count) throws Exception {
        List<String> orderIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            HttpPost request = new HttpPost(url);
            StringEntity entity = new StringEntity(payload);
            request.setEntity(entity);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            request.addHeader(Headers.MAGENTO_ACCESS_TOKEN, "Bearer " + accessToken);
            HttpResponse response = httpClient.execute(request);
            MagentoOrderObject magentoOrderObject = objectMapper.readValue(response.getEntity().getContent(), MagentoOrderObject.class);
            LOG.info("Created Magento Order with Order ID : {}", magentoOrderObject.getIncrementId());
            orderIds.add(magentoOrderObject.getIncrementId());
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed with HTTP error code : " + response.getStatusLine().getStatusCode());
            }
        }
        return orderIds;
    }
}