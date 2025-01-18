package com.flow.microservices.order_service.dto;

import java.math.BigDecimal;

public record OrderReqest(Long id , String orderNumber, String skuCode, BigDecimal price, Integer quantity) {
}
