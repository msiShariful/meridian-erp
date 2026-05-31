package com.erp.ecommerce.service;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Session-scoped shopping cart. Stores product id to quantity mappings plus an
 * optional applied coupon code. Kept serializable so it survives in HttpSession.
 */
@Getter
@Setter
public class Cart implements Serializable {

    /** Maps product id (as String) -> quantity, preserving insertion order. */
    private Map<String, Integer> items = new LinkedHashMap<>();

    private String couponCode;

    public void add(UUID productId, int qty) {
        if (productId == null || qty <= 0) return;
        String key = productId.toString();
        items.merge(key, qty, Integer::sum);
    }

    public void update(UUID productId, int qty) {
        if (productId == null) return;
        String key = productId.toString();
        if (qty <= 0) {
            items.remove(key);
        } else {
            items.put(key, qty);
        }
    }

    public void remove(UUID productId) {
        if (productId != null) items.remove(productId.toString());
    }

    public void clear() {
        items.clear();
        couponCode = null;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getTotalQuantity() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }
}
