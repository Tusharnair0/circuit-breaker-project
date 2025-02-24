package com.Qloron.PaymentService.controller;

import com.Qloron.PaymentService.model.PaymentEntity;
import com.Qloron.PaymentService.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pay")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    private int requestCount = 0;

    @GetMapping
    public ResponseEntity<String> processPayment(){
        requestCount++;
        if(requestCount % 5 != 0) {
            paymentRepository.save(new PaymentEntity("FAILED"));
            throw new RuntimeException("Simulated failure");
        }
        paymentRepository.save(new PaymentEntity("SUCCESS"));
        return ResponseEntity.ok("Payment Successful!");
    }
}
