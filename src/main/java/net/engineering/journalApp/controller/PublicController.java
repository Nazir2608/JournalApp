package net.engineering.journalApp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import net.engineering.journalApp.entity.User;
import net.engineering.journalApp.service.UserService;

@RestController
@RequestMapping("/public")
public class PublicController {
    
    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(PublicController.class);
    
    @PostMapping("create-user")
    public ResponseEntity<?> saveJournalEntry(@Valid @RequestBody User user) {
        try {
            userService.saveUser(user);
            logger.info("New user '{}' created successfully.", user.getUserName());
            return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to create user '{}': {}", user.getUserName(), e.getMessage(), e);
            return new ResponseEntity<>("Error creating user: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
