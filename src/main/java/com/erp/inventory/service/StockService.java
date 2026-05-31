package com.erp.inventory.service;

import com.erp.inventory.entity.Product;
import com.erp.inventory.entity.StockMovement;
import com.erp.inventory.entity.Warehouse;
import com.erp.inventory.enums.MovementType;
import com.erp.inventory.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockService {

    private final StockMovementRepository movementRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public Page<StockMovement> history(Pageable pageable) {
        return movementRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    /**
     * Records a stock movement and adjusts the product's on-hand quantity.
     * IN/ADJUSTMENT add to stock, OUT/TRANSFER subtract (floored at zero).
     */
    public StockMovement recordMovement(Product product, Warehouse warehouse, MovementType type,
                                        int quantity, String reason, String reference) {
        int delta = switch (type) {
            case IN, ADJUSTMENT -> quantity;
            case OUT, TRANSFER -> -quantity;
        };
        int updated = Math.max(0, product.getStockQuantity() + delta);
        product.setStockQuantity(updated);
        productService.save(product);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .warehouse(warehouse)
                .type(type)
                .quantity(quantity)
                .reason(reason)
                .reference(reference)
                .build();
        return movementRepository.save(movement);
    }
}
