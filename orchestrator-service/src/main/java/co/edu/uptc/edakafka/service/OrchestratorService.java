package co.edu.uptc.edakafka.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import co.edu.uptc.edakafka.model.Customer;
import co.edu.uptc.edakafka.model.Login;
import co.edu.uptc.edakafka.utils.JsonUtils;

@Service
public class OrchestratorService {
    private final Logger log = LoggerFactory.getLogger(OrchestratorService.class);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void startCustomerRegistrationSaga(Customer customer) {
        log.info("SAGA INICIADO: Solicitando la creaci�n del cliente: {}", customer.getDocument());
        String customerJson = JsonUtils.toJson(customer);
        kafkaTemplate.send("create-customer-cmd", customerJson);
    }

    @KafkaListener(topics = "customer-created-event", groupId = "saga-orchestrator-group")
    public void handleCustomerCreated(String message) {
        log.info("SAGA PASO 2: El cliente fue creado con �xito. Mensaje recibido: {}", message);
        Customer customer = JsonUtils.fromJson(message, Customer.class);
        Login login = new Login();
        login.setUsername(customer.getEmail());
        login.setPassword(customer.getDocument());
        login.setCustomerId(customer.getDocument());
        String loginJson = JsonUtils.toJson(login);
        log.info("SAGA PASO 3: Solicitando creaci�n de cuenta/login...");
        kafkaTemplate.send("create-login-cmd", loginJson);
    }

    @KafkaListener(topics = "login-created-event", groupId = "saga-orchestrator-group")
    public void handleLoginCreated(String message) {
        log.info("SAGA FINALIZADA CON �XITO: El login fue creado. Registro completado.");
    }

    @KafkaListener(topics = "login-failed-event", groupId = "saga-orchestrator-group")
    public void handleLoginFailed(String message) {
        log.error("SAGA FALLIDA: No se pudo crear el login. Iniciando reversi�n (Compensaci�n)!");
        Login login = JsonUtils.fromJson(message, Login.class);
        String customerId = login.getCustomerId();
        if (customerId == null || customerId.isBlank()) {
            log.error("No customerId disponible para undo-customer-cmd, no se puede compensar.");
            return;
        }
        kafkaTemplate.send("undo-customer-cmd", customerId);
    }
}
