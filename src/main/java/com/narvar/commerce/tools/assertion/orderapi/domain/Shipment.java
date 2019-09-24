
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
        "items_info",
        "ship_method",
        "carrier",
        "carrier_service",
        "ship_source",
        "ship_date",
        "ship_discount",
        "ship_tax",
        "ship_total",
        "tracking_number",
        "shipped_from",
        "shipped_to"
})
public class Shipment {

    @JsonProperty("items_info")
    public List<ItemsInfo> itemsInfo = null;
    @JsonProperty("ship_method")
    public String shipMethod;
    @JsonProperty("carrier")
    public String carrier;
    @JsonProperty("carrier_service")
    public String carrierService;
    @JsonProperty("ship_source")
    public String shipSource;
    @JsonProperty("ship_date")
    public String shipDate;
    @JsonProperty("ship_discount")
    public String shipDiscount;
    @JsonProperty("ship_tax")
    public String shipTax;
    @JsonProperty("ship_total")
    public String shipTotal;
    @JsonProperty("tracking_number")
    public String trackingNumber;
    @JsonProperty("shipped_from")
    public ShippedFrom shippedFrom;
    @JsonProperty("shipped_to")
    public ShippedTo shippedTo;
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
