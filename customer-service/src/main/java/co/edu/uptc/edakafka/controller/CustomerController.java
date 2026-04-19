package co.edu.uptc.edakafka.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uptc.edakafka.model.Customer;
import co.edu.uptc.edakafka.service.CustomerEventProducer;
import co.edu.uptc.edakafka.service.CustomerService;
import co.edu.uptc.edakafka.utils.JsonUtils;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerEventProducer customerEventProducer;
    @Autowired
    private CustomerService customerService;

    private static JsonUtils jsonUtils = new JsonUtils();

    @PostMapping("/add")
    public ResponseEntity<String> addCustomer(@RequestBody Customer customer) {
        customerEventProducer.sendAddCustomerEvent(customer);
        return ResponseEntity.ok("Evento ADD enviado para customer: " + customer.getDocument());
    }

    @PutMapping("/edit")
    public ResponseEntity<String> editCustomer(@RequestBody Customer customer) {
        customerEventProducer.sendEditCustomerEvent(customer);
        return ResponseEntity.ok("Evento EDIT enviado para customer: " + customer.getDocument());
    }

    @DeleteMapping("/delete/{document}")
    public ResponseEntity<String> deleteCustomer(@PathVariable String document) {
        Customer customer = customerService.findById(document);
        if (customer == null) return ResponseEntity.status(404).body("Customer no encontrado: " + document);
        boolean deleted = customerService.delete(customer);
        return deleted ? ResponseEntity.ok("Customer eliminado: " + document)
                       : ResponseEntity.status(500).body("Error al eliminar: " + document);
    }

    @GetMapping("/find/{document}")
    public ResponseEntity<?> findCustomerById(@PathVariable String document) {
        customerEventProducer.sendFindByCustomerIDEvent(document);
        Customer customer = customerService.findById(document);
        if (customer == null) return ResponseEntity.status(404).body("Customer no encontrado: " + document);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/findall")
    public ResponseEntity<List<Customer>> findAllCustomers() {
        customerEventProducer.sendFindAllOrdersEvent("findall");
        return ResponseEntity.ok(customerService.findAll());
    }
}
