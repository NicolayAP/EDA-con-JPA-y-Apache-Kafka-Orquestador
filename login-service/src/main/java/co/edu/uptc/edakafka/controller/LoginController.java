package co.edu.uptc.edakafka.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uptc.edakafka.model.Login;
import co.edu.uptc.edakafka.service.LoginService;

@RestController
@RequestMapping("/api/logins")
public class LoginController {

    @Autowired
    private LoginService loginService;

    // Endpoint para POST /api/logins
    @PostMapping
    public ResponseEntity<Login> createLogin(@RequestBody Login login) {
        Login createdLogin = loginService.createLogin(login);
        return new ResponseEntity<>(createdLogin, HttpStatus.CREATED);
    }

    // Endpoint para GET /api/logins
    @GetMapping
    public ResponseEntity<List<Login>> getAllLogins() {
        return ResponseEntity.ok(loginService.getAllLogins());
    }

    // Endpoint para GET /api/logins/customer/{customerId}
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Login> getLoginByCustomerId(@PathVariable String customerId) {
        Optional<Login> login = loginService.getLoginByCustomerId(customerId);
        return login.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
