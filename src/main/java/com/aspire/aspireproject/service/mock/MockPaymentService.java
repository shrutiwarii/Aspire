package com.aspire.aspireproject.service.mock;

import org.springframework.stereotype.Service;

@Service
public class MockPaymentService {
    public boolean processPayment(Double amount, String paymentSource, Integer termNo) {
        // Simulate payment processing logic.
        // You can return true for successful payments and false for failures.
        // You may also log payment details for demonstration purposes.
        // Sleep for a while to simulate payment processing time.
        try {
            Thread.sleep(5000); // Sleep for 5 seconds to simulate payment processing.
            System.out.println("Payment in progress...");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Payment successful of amount "+amount+" via "+paymentSource+" for term number "+termNo);
        return true; // Simulate a successful payment.
    }

}
