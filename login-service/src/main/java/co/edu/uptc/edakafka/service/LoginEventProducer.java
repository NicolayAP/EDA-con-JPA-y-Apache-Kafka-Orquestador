package co.edu.uptc.edakafka.service;

import co.edu.uptc.edakafka.model.Login;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LoginEventProducer {

    private static final String TOPIC = "login-events";
    private static final String TOPIC_LOGIN_SAGA_CREATED = "login-created-event";
    private static final String TOPIC_LOGIN_SAGA_FAILED = "login-failed-event";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendLoginEvent(Login login, String eventType) {
        try {
            String message = objectMapper.writeValueAsString(login);
            kafkaTemplate.send(TOPIC, eventType, message);
            System.out.println("Evento de Login enviado [" + eventType + "]: " + message);
        } catch (JsonProcessingException e) {
            System.err.println("Error procesando JSON para el evento de Login: " + e.getMessage());
        }
    }

    public void sendLoginCreatedEvent(Login login) {
        try {
            String json = objectMapper.writeValueAsString(login);
            kafkaTemplate.send(TOPIC_LOGIN_SAGA_CREATED, "LOGIN_CREATED_SUCCESS", json);
            System.out.println("[LOGIN PRODUCER] SAGA: login-created-event enviado: " + json);
        } catch (Exception e) {
            System.err.println("Error procesando login " + e.getMessage());
        }
    }

    public void sendLoginFailedEvent(Login login, String reason) {
        try {
            String json = objectMapper.writeValueAsString(login);
            kafkaTemplate.send(TOPIC_LOGIN_SAGA_FAILED, "LOGIN_FAILED", json);
            System.out.println("[LOGIN PRODUCER] SAGA: login-failed-event enviado: " + json + " - Reason: " + reason);
        } catch (Exception e) {
            System.err.println("Error procesando login " + e.getMessage());
        }
    }
}
