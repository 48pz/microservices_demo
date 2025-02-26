package com.flow.microservices.order_service.service;

import com.flow.microservices.order_service.client.InventoryClient;
import com.flow.microservices.order_service.dto.OrderRequest;
import com.flow.microservices.order_service.event.OrderPlacedEvent;
import com.flow.microservices.order_service.model.Order;
import com.flow.microservices.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest) {

        //use wiremock to mock the inventory service. Like testing the service without the actual service
        boolean isInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if (isInStock) {
            // map orderReqest to Order entity
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price());
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());
            //save Order entity to database
            orderRepository.save(order);

            //send the message to kafka topic
            //ordernumber and email
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
            orderPlacedEvent.setOrderNumber(order.getOrderNumber());
            orderPlacedEvent.setEmail(orderRequest.userDetails().email());
            orderPlacedEvent.setFirstName(orderRequest.userDetails().firstName());
            orderPlacedEvent.setLastName(orderRequest.userDetails().lastName());
            log.info("start: sending orderplacedevent {} to kafka topic order-placed",orderPlacedEvent);
            kafkaTemplate.send("order-placed",orderPlacedEvent);
            log.info("end: sending orderplacedevent {} to kafka topic order-placed",orderPlacedEvent);
        } else {
            throw new RuntimeException("Product with SkuCode " + orderRequest.skuCode() + " is out of stock");
        }
    }
}
