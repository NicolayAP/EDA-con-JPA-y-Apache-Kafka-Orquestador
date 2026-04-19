package co.edu.uptc.edakafka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import co.edu.uptc.edakafka.model.Customer;
import co.edu.uptc.edakafka.utils.JsonUtils;

@Service
public class CustomerEventConsumer {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerEventProducer customerEventProducer;

    @KafkaListener(topics = "create-customer-cmd", groupId = "customer_group")
    public void handleCreateCustomerCmd(String message) {
        System.out.println("[CUSTOMER CONSUMER] recv create-customer-cmd: " + message);
        Customer customer = JsonUtils.fromJson(message, Customer.class);
        boolean saved = customerService.save(customer);
        if (saved) {
            System.out.println("[CUSTOMER CONSUMER] Saved! Sending customer-created-event...");
            customerEventProducer.sendCustomerCreatedEvent(customer);
        } else {
            System.err.println("[CUSTOMER CONSUMER] Failed to save customer, no saga event sent.");
        }
    }

    @KafkaListener(topics = "undo-customer-cmd", groupId = "customer_group")
    public void handleUndoCustomerCmd(String message) {
        System.out.println("[CUSTOMER CONSUMER] recv undo-customer-cmd: " + message);
        try {
            boolean deleted = customerService.deleteById(message);
            if (deleted) {
                System.out.println("[CUSTOMER CONSUMER] Undo successful for customerId=" + message);
            } else {
                System.err.println("[CUSTOMER CONSUMER] Undo failed for customerId=" + message);
            }
        } catch (Exception e) {
            System.err.println("Failed undo " + e.getMessage());
        }
    }
}
