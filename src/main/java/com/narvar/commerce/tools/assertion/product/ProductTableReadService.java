package com.narvar.commerce.tools.assertion.product;

import com.narvar.commerce.tools.assertion.domain.ProductAssertionResponse;
import com.narvar.commerce.tools.assertion.shopify.domain.ShopifyProduct;
import com.narvar.commerce.tools.assertion.shopify.domain.ShopifyVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductTableReadService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductTableReadService.class);

    public ProductAssertionResponse validateProducts(String storeName, List<ShopifyProduct> shopifyProductList) {
        return readProductsTable(storeName, shopifyProductList);
    }

    public ProductAssertionResponse readProductsTable(String storeName, List<ShopifyProduct> shopifyProductList) {
        ProductAssertionResponse productAssertionResponse = new ProductAssertionResponse();
        productAssertionResponse.setMessage("SUCCESS");
        productAssertionResponse.setSuccess(true);
        String user = "username";
        String bastionHost = "bastion-qa01.narvar.qa";
        Integer size = shopifyProductList.size();
        Integer currentIndex = 0;
        List<Long> validProductIds = new ArrayList<>();
        List<Long> validVarientIds = new ArrayList<>();
        List<Long> invalidProductIds = new ArrayList<>();
        List<Long> invalidVarientids = new ArrayList<>();
        Map<Long, Long> varientMap = new HashMap<>();

        List<Long> varientIds = new ArrayList<>();
        while (currentIndex < size) {
            ShopifyProduct currentProduct = shopifyProductList.get(currentIndex);
            for (ShopifyVariant shopifyVariant : currentProduct.getShopifyVariants()) {
                varientIds.add(shopifyVariant.getId());
                varientMap.put(shopifyVariant.getId(), currentProduct.getId());
            }
            currentIndex++;
        }

        String commandCount = "/usr/bin/ssh -A " + user + "@" + bastionHost + " " +
                " cqlsh yugabyte.narvar.internal -e \"SELECT count(*) FROM indus.products WHERE storename = '" + storeName + "'\"";

        makeDBCallCount(commandCount, varientIds, productAssertionResponse);

        if (productAssertionResponse.isSuccess()) {
            return productAssertionResponse;
        }

        Integer listSize = varientIds.size();
        Integer buffer = 200;
        for (int i = 0; i < listSize / buffer; i++) {
            String command = "/usr/bin/ssh -A " + user + "@" + bastionHost + " " +
                    " cqlsh yugabyte.narvar.internal -e \"SELECT variantid FROM indus.products WHERE storename = '"
                    + storeName + "' limit " + buffer + " offset " + i * buffer + "\"";

            makeDBCall(command, varientIds, validVarientIds, invalidVarientids, validProductIds, invalidProductIds, productAssertionResponse, varientMap);
        }
        productAssertionResponse.setInvalidProductIds(invalidProductIds);
        productAssertionResponse.setInvalidVariantIds(invalidVarientids);
        productAssertionResponse.setValidatedProductIds(validProductIds);
        productAssertionResponse.setValidatedVariantIds(validVarientIds);
        productAssertionResponse.setInvalidProductIdsCount(invalidProductIds.size());
        productAssertionResponse.setInvalidVariantIdsCount(invalidVarientids.size());
        productAssertionResponse.setValidatedProductIdsCount(validProductIds.size());
        productAssertionResponse.setValidatedVariantIdsCount(validVarientIds.size());
        return productAssertionResponse;

    }

    private void makeDBCall(String command, List<Long> varientIds, List<Long> validVarientIds, List<Long> invalidVarientids,
                            List<Long> validProductIds, List<Long> invalidProductIds, ProductAssertionResponse productAssertionResponse, Map<Long, Long> varientMap) {
        try {
            Process p = Runtime.getRuntime().
                    exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            List<String> varientIdsFetched = new ArrayList<>();
            String line = "";
            while ((line = reader.readLine()) != null) {
                varientIdsFetched.add(line.trim());
            }
            LOG.info("VARIENT IDS FETCHED : {}", varientIdsFetched);
            for (Long varientId : varientIds) {
                if (varientIdsFetched.contains(varientId.toString())) {
                    validVarientIds.add(varientId);
                    validProductIds.add(varientMap.get(varientId));
                } else {
                    productAssertionResponse.setMessage("FAILURE");
                    productAssertionResponse.setSuccess(false);
                    invalidVarientids.add(varientId);
                    invalidProductIds.add(varientMap.get(varientId));
                }
            }

        } catch (Exception e) {
            productAssertionResponse.setMessage(e.getMessage());
            productAssertionResponse.setSuccess(false);
            LOG.error("ERROR : ", e);
        }
    }

    private void makeDBCallCount(String command, List<Long> varientIds, ProductAssertionResponse productAssertionResponse) {
        try {
            Process p = Runtime.getRuntime().
                    exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            Integer count = 0;
            String line = "";

            while ((line = reader.readLine()) != null) {
                try {
                    count = Integer.parseInt(line.trim());
                } catch (NumberFormatException | NullPointerException nfe) {

                }
            }
            productAssertionResponse.setExpectedCount(varientIds.size());
            productAssertionResponse.setFetchedCount(count);
            LOG.info("COUNT FETCHED : {}", count);
            LOG.info("COUNT EXPECTED : {}", varientIds.size());
            if (count >= varientIds.size()) {
                LOG.info("COUNT MATCHED");
                productAssertionResponse.setMessage("COUNT MATCHED");
            } else {
                productAssertionResponse.setSuccess(false);
            }

        } catch (Exception e) {
            productAssertionResponse.setMessage(e.getMessage());
            productAssertionResponse.setSuccess(false);
            LOG.error("ERROR : ", e);
        }
    }

}
