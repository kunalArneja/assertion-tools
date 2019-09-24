/*
 *
 * Copyright (c) 2019 Narvar Inc.
 * All rights reserved
 *
 */

package com.narvar.commerce.tools.assertion.shopify.service;


import com.narvar.commerce.tools.assertion.shopify.constants.ShopifyRequestConstants;
import com.narvar.commerce.tools.assertion.utils.DateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

public class ShopifyUrl {
    private static final Logger LOG = LoggerFactory.getLogger(ShopifyUrl.class);

    private String urlPath;
    private LocalDateTime updateAtMin;
    private LocalDateTime createdAtMin;
    private LocalDateTime createdAtMax;
    private Integer page;
    private Integer limit;
    private String sortOrderKey;
    private ShopifyRequestConstants.SortOrder sortOrder;
    private Set<Long> ids;
    private ShopifyRequestConstants.Status status;

    private ShopifyUrl() {

    }

    public static ShopifyUrlBuilder builder() {
        return new ShopifyUrlBuilder();
    }


    public static class ShopifyUrlBuilder {
        private String urlPath;
        private LocalDateTime updateAtMin;
        private LocalDateTime updateAtMax;
        private LocalDateTime createdAtMin;
        private LocalDateTime createdAtMax;
        private Integer page;
        private Integer limit;
        private ShopifyRequestConstants.SortOrderKey sortOrderKey;
        private ShopifyRequestConstants.SortOrder sortOrder;
        private Set<Long> ids;
        private Long sinceId;
        private ShopifyRequestConstants.Status status;

        ShopifyUrlBuilder() {
        }

        public ShopifyUrlBuilder urlPath(String urlPath) {
            this.urlPath = urlPath;
            return this;
        }

        public ShopifyUrlBuilder updateAtMin(LocalDateTime updateAtMin) {
            this.updateAtMin = updateAtMin;
            return this;
        }

        public ShopifyUrlBuilder updateAtMax(LocalDateTime updateAtMax) {
            this.updateAtMax = updateAtMax;
            return this;
        }

        public ShopifyUrlBuilder createdAtMin(LocalDateTime createdAtMin) {
            this.createdAtMin = createdAtMin;
            return this;
        }

        public ShopifyUrlBuilder createdAtMax(LocalDateTime createdAtMax) {
            this.createdAtMax = createdAtMax;
            return this;
        }

        public ShopifyUrlBuilder page(Integer page) {
            this.page = page;
            return this;
        }

        public ShopifyUrlBuilder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public ShopifyUrlBuilder sortOrderKey(ShopifyRequestConstants.SortOrderKey sortOrderKey) {
            this.sortOrderKey = sortOrderKey;
            return this;
        }

        public ShopifyUrlBuilder sortOrder(ShopifyRequestConstants.SortOrder sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        public ShopifyUrlBuilder ids(Set<Long> ids) {
            this.ids = ids;
            return this;
        }

        public ShopifyUrlBuilder sinceId(Long sinceId) {
            this.sinceId = sinceId;
            return this;
        }

        public ShopifyUrlBuilder status(ShopifyRequestConstants.Status status) {
            this.status = status;
            return this;
        }

        public String build() throws Exception {
            try {
                URIBuilder builder = new URIBuilder();
                builder.setPath(urlPath);

                if (Objects.nonNull(updateAtMin)) {
                    builder.addParameter(ShopifyRequestConstants.UPDATED_AT_MIN, DateUtils.getDateInUTC(updateAtMin));
                }
                if (Objects.nonNull(page)) {
                    builder.addParameter(ShopifyRequestConstants.PAGE, String.valueOf(page));
                }
                if (Objects.nonNull(limit)) {
                    builder.addParameter(ShopifyRequestConstants.LIMIT, String.valueOf(limit));
                }
                if (Objects.nonNull(sinceId)) {
                    builder.addParameter(ShopifyRequestConstants.SINCE_ID, String.valueOf(sinceId));
                }
                if (CollectionUtils.isNotEmpty(ids)) {
                    builder.addParameter(ShopifyRequestConstants.IDS, StringUtils.join(ids, ","));
                }
                if (Objects.nonNull(sortOrderKey)) {
                    String sortOrderValue = sortOrderKey.toString();
                    if (Objects.nonNull(sortOrder)) {
                        sortOrderValue = sortOrderValue + "+" + sortOrder.toString();
                    }
                    builder.addParameter(ShopifyRequestConstants.SORT_ORDER, sortOrderValue);
                }
                if (Objects.nonNull(createdAtMin)) {
                    builder.addParameter(ShopifyRequestConstants.CREATED_AT_MIN, DateUtils.getDateInUTC(createdAtMin));
                }
                if (Objects.nonNull(createdAtMax)) {
                    builder.addParameter(ShopifyRequestConstants.CREATED_AT_MAX, DateUtils.getDateInUTC(createdAtMax));
                }
                if (Objects.nonNull(updateAtMax)) {
                    builder.addParameter(ShopifyRequestConstants.UPDATED_AT_MAX, DateUtils.getDateInUTC(updateAtMax));
                }
                if (Objects.nonNull(status)) {
                    builder.addParameter(ShopifyRequestConstants.STATUS, status.getStatus());
                }

                return URLDecoder.decode(builder.build().toString(), StandardCharsets.UTF_8.toString());
            } catch (URISyntaxException | UnsupportedEncodingException e) {
                LOG.error("Unable to build URL. ", e);
                throw new Exception("Unable to build url.");
            }
        }

        @Override
        public String toString() {
            return "ShopifyUrlBuilder{" +
                    "urlPath='" + urlPath + '\'' +
                    ", updateAtMin=" + updateAtMin +
                    ", updateAtMax=" + updateAtMax +
                    ", createdAtMin=" + createdAtMin +
                    ", createdAtMax=" + createdAtMax +
                    ", page=" + page +
                    ", limit=" + limit +
                    ", sortOrderKey=" + sortOrderKey +
                    ", sortOrder=" + sortOrder +
                    ", ids=" + ids +
                    ", sinceId=" + sinceId +
                    ", status=" + status +
                    '}';
        }
    }
}
