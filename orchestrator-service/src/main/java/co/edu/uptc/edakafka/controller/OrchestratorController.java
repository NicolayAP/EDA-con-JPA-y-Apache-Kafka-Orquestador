package co.edu.uptc.edakafka.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import co.edu.uptc.edakafka.model.Customer;
import co.edu.uptc.edakafka.service.OrchestratorService;

@RestController
@RequestMapping("/orchestrator")
public class OrchestratorController {
    @Autowired
    private OrchestratorService orchestratorService;

    @PostMapping("/register")
    public String registerCustomer(@RequestBody Customer customer) {
        orchestratorService.startCustomerRegistrationSaga(customer);
        return "Saga started for customer: " + customer.getDocument();
    }
}
