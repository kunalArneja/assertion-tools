
/*
 * *
 *  * Copyright (c) 2019 Narvar Inc.
 *  * All rights reserved
 *
 */

package com.narvar.commerce.tools.assertion.orderapi.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "order_number",
        "order_date",
        "order_items",
        "shipments",
        "billing",
        "pickups",
        "customer",
        "attributes",
        "events",
        "promotions",
        "currency_code",
        "order_promise_date",
        "selected_ship_method"
})
public class OrderInfo {

    @JsonProperty("order_number")
    public Integer orderNumber;
    @JsonProperty("order_date")
    public String orderDate;
    @JsonProperty("order_items")
    public List<OrderItem> orderItems = null;
    @JsonProperty("shipments")
    public List<Shipment> shipments = null;
    @JsonProperty("billing")
    public Billing billing;
    @JsonProperty("pickups")
    public List<Pickup> pickups = null;
    @JsonProperty("customer")
    public Customer customer;
    @JsonProperty("attributes")
    public Map<String, String> attributes;
    @JsonProperty("events")
    public List<Event> events = null;
    @JsonProperty("promotions")
    public List<Promotion> promotions = null;
    @JsonProperty("currency_code")
    public String currencyCode;
    @JsonProperty("order_promise_date")
    public String orderPromiseDate;
    @JsonProperty("selected_ship_method")
    public String selectedShipMethod;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
