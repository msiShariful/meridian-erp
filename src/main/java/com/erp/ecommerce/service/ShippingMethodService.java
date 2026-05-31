package com.erp.ecommerce.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.ecommerce.entity.ShippingMethod;
import com.erp.ecommerce.repository.ShippingMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShippingMethodService {

    private final ShippingMethodRepository shippingMethodRepository;

    @Transactional(readOnly = true)
    public List<ShippingMethod> all() {
        return shippingMethodRepository.findAll(Sort.by(Sort.Direction.ASC, "rate"));
    }

    @Transactional(readOnly = true)
    public ShippingMethod get(UUID id) {
        return shippingMethodRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Shipping method", id));
    }

    /** Resolves the shipping charge for a method given the order subtotal. */
    public BigDecimal chargeFor(ShippingMethod method, BigDecimal subtotal) {
        if (method == null) return BigDecimal.ZERO;
        if (method.getFreeAbove() != null && subtotal != null
                && subtotal.compareTo(method.getFreeAbove()) >= 0) {
            return BigDecimal.ZERO;
        }
        return method.getRate();
    }

    public ShippingMethod save(ShippingMethod method) {
        return shippingMethodRepository.save(method);
    }

    public void delete(UUID id) {
        shippingMethodRepository.deleteById(id);
    }
}
