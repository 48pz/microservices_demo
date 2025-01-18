package com.flow.microservices.order_service.service;

import com.flow.microservices.order_service.client.InventoryClient;
import com.flow.microservices.order_service.dto.OrderReqest;
import com.flow.microservices.order_service.model.Order;
import com.flow.microservices.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public void placeOrder(OrderReqest orderReqest) {

        //use wiremock to mock the inventory service. Like testing the service without the actual service
        boolean isInStock = inventoryClient.isInStock(orderReqest.skuCode(), orderReqest.quantity());

        if (!isInStock) {
            throw new RuntimeException("Product with skuCode " + orderReqest.skuCode() + " is out of stock");
        }
        // map orderReqest to Order entity
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setPrice(orderReqest.price());
        order.setSkuCode(orderReqest.skuCode());
        order.setQuantity(orderReqest.quantity());
        //save Order entity to database
        orderRepository.save(order);
    }
}
