package co.edu.uptc.edakafka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.uptc.edakafka.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {

}

