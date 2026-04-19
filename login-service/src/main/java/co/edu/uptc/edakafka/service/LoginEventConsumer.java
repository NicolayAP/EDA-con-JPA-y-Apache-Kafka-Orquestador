package co.edu.uptc.edakafka.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import co.edu.uptc.edakafka.model.Login;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LoginEventConsumer {

    @Autowired
    private LoginService loginService;

    @Autowired
    private LoginEventProducer loginEventProducer;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "login-events", groupId = "login_group")
    public void consume(ConsumerRecord<String, String> record) {
        String eventType = record.key();
        String message = record.value();
        System.out.println("[LOGIN CONSUMER] EventType=" + eventType + ", message=" + message);
    }

    @KafkaListener(topics = "create-login-cmd", groupId = "login_group")
    public void handleCreateLoginCmd(String message) {
        System.out.println("[LOGIN CONSUMER] recv create-login-cmd: " + message);
        Login login = null;
        try {
            login = objectMapper.readValue(message, Login.class);
            loginService.createLogin(login);
            loginEventProducer.sendLoginCreatedEvent(login);
            System.out.println("[LOGIN CONSUMER] SAGA SUCCESS: sent login-created-event");
        } catch (Exception e) {
            System.err.println("[LOGIN CONSUMER] SAGA FAILED: " + e.getMessage());
            if (login == null) {
                login = new Login(); // Empty login to denote failure if parsing failed
            }
            loginEventProducer.sendLoginFailedEvent(login, e.getMessage());
        }
    }
}
