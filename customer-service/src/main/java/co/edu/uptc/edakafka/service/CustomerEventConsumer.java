package co.edu.uptc.edakafka.service;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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

    @KafkaListener(topics = "customer-events", groupId = "customer_group")
    public void handleCustomerEvent(ConsumerRecord<String, String> record) {
        // ...existing code... we don't care too much, maybe keep it to avoid compile issues, but we better keep it basic
        String eventType = record.key();
        String payload = record.value();
        if ("CUSTOMER_CREATED".equals(eventType)) {
            Customer customer = JsonUtils.fromJson(payload, Customer.class);
            customerService.save(customer);
        }
    }

    // Saga Endpoints
    @KafkaListener(topics = "create-customer-cmd", groupId = "customer_group")
    public void handleCreateCustomerCmd(String message) {
        System.out.println("[CUSTOMER CONSUMER] recv create-customer-cmd: " + message);
        Customer customer = JsonUtils.fromJson(message, Customer.class);
        boolean saved = customerService.save(customer);
        if (saved) {
            System.out.println("[CUSTOMER CONSUMER] Saved! Sending customer-created-event...");
            customerEventProducer.sendCustomerCreatedEvent(customer);
        }
    }

    @KafkaListener(topics = "undo-customer-cmd", groupId = "customer_group")
    public void handleUndoCustomerCmd(String message) {
        System.out.println("[CUSTOMER CONSUMER] recv undo-customer-cmd: " + message);
        try {
            Customer customer = JsonUtils.fromJson(message, Customer.class);
            customerService.delete(customer);
            System.out.println("[CUSTOMER CONSUMER] Undo successful!");
        } catch (Exception e) {
            System.err.println("Failed undo " + e.getMessage());
        }
    }
}
