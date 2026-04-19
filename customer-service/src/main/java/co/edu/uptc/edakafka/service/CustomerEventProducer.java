package co.edu.uptc.edakafka.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import co.edu.uptc.edakafka.model.Customer;
import co.edu.uptc.edakafka.utils.JsonUtils;

@Service
public class CustomerEventProducer {
    private static final String TOPIC_CUSTOMER_EVENTS = "customer-events";
    private static final String EVENT_CREATE = "CUSTOMER_CREATED";
    private static final String EVENT_UPDATE = "CUSTOMER_UPDATED";
    private static final String EVENT_DELETE = "CUSTOMER_DELETED";
    private static final String EVENT_FIND_BY_ID = "CUSTOMER_FIND_BY_ID";
    private static final String EVENT_FIND_ALL = "CUSTOMER_FIND_ALL";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private void sendCustomerEvent(String eventType, String payload) {
        kafkaTemplate.send(TOPIC_CUSTOMER_EVENTS, eventType, payload);
        System.out.println("[CUSTOMER PRODUCER] " + eventType + " enviado: " + payload);
    }

    public void sendAddCustomerEvent(Customer customer) {
        String json = JsonUtils.toJson(customer);
        sendCustomerEvent(EVENT_CREATE, json);
    }

    public void sendEditCustomerEvent(Customer customer) {
        String json = JsonUtils.toJson(customer);
        sendCustomerEvent(EVENT_UPDATE, json);
    }

    public void sendDeleteCustomerEvent(String document) {
        sendCustomerEvent(EVENT_DELETE, document);
    }

    public void sendFindByCustomerIDEvent(String document) {
        sendCustomerEvent(EVENT_FIND_BY_ID, document);
    }

    public void sendFindAllOrdersEvent(String trigger) {
        sendCustomerEvent(EVENT_FIND_ALL, trigger);
    }

    // New method for Saga
    public void sendCustomerCreatedEvent(Customer customer) {
        String json = JsonUtils.toJson(customer);
        kafkaTemplate.send("customer-created-event", json);
        System.out.println("[CUSTOMER PRODUCER] SAGA: customer-created-event enviado: " + json);
    }
}
