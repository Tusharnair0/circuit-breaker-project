package com.Qloron.OrderService.controller;

import com.Qloron.OrderService.model.OrderEntity;
import com.Qloron.OrderService.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    private static final String ORDER_SERVICE = "orderService";

    @PostMapping("/place")
    @CircuitBreaker(name = ORDER_SERVICE, fallbackMethod = "paymentFallback")
    public ResponseEntity<String> placeOrder(){
        String response = restTemplate.getForObject("http://PAYMENT-SERVICE/pay", String.class);
        orderRepository.save(new OrderEntity("Success"));
        return ResponseEntity.ok("Order placed successfully! " + response);
    }

    public ResponseEntity<Map<String, String>> paymentFallback(Exception e){
        orderRepository.save(new OrderEntity("CIRCUIT_OPEN"));
        Map<String, String> response = new HashMap<>();
        response.put("message", "Payment service is currently unavailable. Please try again later!");
        response.put("status", "FAILED");
        response.put("reason", "Circuit Breaker is OPEN");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

}
