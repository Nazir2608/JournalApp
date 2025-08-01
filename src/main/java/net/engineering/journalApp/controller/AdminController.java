package net.engineering.journalApp.controller;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.engineering.journalApp.entity.User;
import net.engineering.journalApp.service.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @GetMapping("all-user")
    public ResponseEntity<?> getAllUser() {
        try {
            List<User> allUser = userService.getAll();
            if (allUser != null && !allUser.isEmpty()) {
                logger.info("Retrieved {} users", allUser.size());
                return ResponseEntity.ok(allUser);
            } else {
                logger.warn("No users found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found");
            }
        } catch (Exception e) {
            logger.error("Error retrieving users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching users");
        }
    }

    @PostMapping("/create-user-admin")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            user.setRoles(Arrays.asList("ADMIN"));
            userService.saveUser(user);
            logger.info("Admin user '{}' created successfully", user.getUserName());
            return ResponseEntity.status(HttpStatus.CREATED).body("Admin user created successfully");
        } catch (Exception e) {
            logger.error("Error creating admin user", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error registering user: " + e.getMessage());
        }
    }
}
