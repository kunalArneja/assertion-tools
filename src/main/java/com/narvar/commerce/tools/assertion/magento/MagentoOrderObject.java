package com.narvar.commerce.tools.assertion.magento;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MagentoOrderObject {

    @JsonProperty("increment_id")
    private String incrementId;
}
