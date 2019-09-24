
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
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "categories",
        "is_backordered",
        "vendors",
        "vendor",
        "description",
        "discount_amount",
        "discount_percent",
        "final_sale_date",
        "anticipated_ship_date",
        "fulfillment_status",
        "is_final_sale",
        "product_type",
        "product_id",
        "is_gift",
        "item_id",
        "item_image",
        "item_promise_date",
        "item_url",
        "line_number",
        "name",
        "quantity",
        "sku",
        "unit_price",
        "fulfillment_type",
        "color",
        "size",
        "style",
        "dimensions",
        "attributes",
        "events"
})
public class OrderItem {

    @JsonProperty("categories")
    public List<String> categories = null;
    @JsonProperty("is_backordered")
    public Boolean isBackordered;
    @JsonProperty("vendor")
    public String vendor;
    @JsonProperty("vendors")
    public List<Vendor> vendors = null;
    @JsonProperty("description")
    public String description;
    @JsonProperty("discount_amount")
    public String discountAmount;
    @JsonProperty("discount_percent")
    public String discountPercent;
    @JsonProperty("final_sale_date")
    public String finalSaleDate;
    @JsonProperty("anticipated_ship_date")
    public String anticipatedShipDate;
    @JsonProperty("fulfillment_status")
    public String fulfillmentStatus;
    @JsonProperty("is_final_sale")
    public Boolean isFinalSale;
    @JsonProperty("product_type")
    public String productType;
    @JsonProperty("product_id")
    public String productId;
    @JsonProperty("is_gift")
    public Boolean isGift;
    @JsonProperty("item_id")
    public Long itemId;
    @JsonProperty("item_image")
    public String itemImage;
    @JsonProperty("item_promise_date")
    public String itemPromiseDate;
    @JsonProperty("item_url")
    public String itemUrl;
    @JsonProperty("line_number")
    public Integer lineNumber;
    @JsonProperty("name")
    public String name;
    @JsonProperty("quantity")
    public Integer quantity;
    @JsonProperty("sku")
    public String sku;
    @JsonProperty("unit_price")
    public String unitPrice;
    @JsonProperty("fulfillment_type")
    public String fulfillmentType;
    @JsonProperty("color")
    public String color;
    @JsonProperty("size")
    public String size;
    @JsonProperty("style")
    public String style;
    @JsonProperty("dimensions")
    public Dimensions dimensions;
    @JsonProperty("attributes")
    public Map<String, String> attributes;
    @JsonProperty("events")
    public List<Event> events = null;
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
