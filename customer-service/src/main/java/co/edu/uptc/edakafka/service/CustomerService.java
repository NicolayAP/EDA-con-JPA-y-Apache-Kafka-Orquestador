package co.edu.uptc.edakafka.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.uptc.edakafka.model.Customer;
import co.edu.uptc.edakafka.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public boolean save(Customer customer) {
        try {
            customerRepository.saveAndFlush(customer);
            return true;
        } catch (Exception e) {
            System.out.println("[SERVICE] Error guardando: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(Customer customer) {
        try {
            customerRepository.delete(customer);
            return true;
        } catch (Exception e) {
            System.out.println("[SERVICE] Error eliminando: " + e.getMessage());
            return false;
        }
    }

    public Customer findById(String document) {
        Optional<Customer> opt = customerRepository.findById(document);
        return opt.orElse(null);
    }

    public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
        customerRepository.findAll().forEach(list::add);
        return list;
    }
}