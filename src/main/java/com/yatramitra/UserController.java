package com.yatramitra;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "app_user")
class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    User findByEmail(String email);
}

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<String> register(@RequestParam String name, @RequestParam String email, @RequestParam String password) {
        try {
            if (userRepository.existsByEmail(email)) {
                return ResponseEntity.badRequest().body("Account already exists with this email.");
            }
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            userRepository.save(user);
            return ResponseEntity.ok("Registration Successful");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Registration error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest().body("Account not found. Please register.");
            }
            if (!user.getPassword().equals(password)) {
                return ResponseEntity.badRequest().body("Incorrect password.");
            }
            return ResponseEntity.ok("Login Successful");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Login error: " + e.getMessage());
        }
    }

    @GetMapping("/user-profile")
    public ResponseEntity<?> getUserProfile(@RequestParam String email) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Profile error: " + e.getMessage());
        }
    }
}
