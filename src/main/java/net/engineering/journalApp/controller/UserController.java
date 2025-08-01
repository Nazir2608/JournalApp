package net.engineering.journalApp.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import net.engineering.journalApp.entity.User;
import net.engineering.journalApp.repository.UserRepository;
import net.engineering.journalApp.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // REGISTER a new user
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            user.setRoles(Arrays.asList("USER"));
            userService.saveUser(user);  // only for new users
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error registering user: " + e.getMessage());
        }
    }

    // UPDATE currently logged-in user details (username, password)
    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User updateUser) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User userInDB = userService.findByUserName(userName);

            if (updateUser.getUserName() != null) {
                userInDB.setUserName(updateUser.getUserName());
            }
            if (updateUser.getPassword() != null) {
                userInDB.setPassword(updateUser.getPassword()); // will re-encode if password changed
            }

            userService.saveUser(userInDB);
            return ResponseEntity.ok(userInDB);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found or could not be updated");
        }
    }

    // DELETE currently logged-in user
    @DeleteMapping
    public ResponseEntity<?> deleteUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            long deletedCount = userRepository.deleteByUserName(userName);

            if (deletedCount > 0) {
                return ResponseEntity.ok("User deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No user found with username: " + userName);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the user");
        }
    }
}
