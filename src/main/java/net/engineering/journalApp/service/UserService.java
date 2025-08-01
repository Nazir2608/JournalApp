package net.engineering.journalApp.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import net.engineering.journalApp.entity.User;
import net.engineering.journalApp.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	public void saveUser(User user) {
		try {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userRepository.save(user);
		} catch (Exception e) {
			logger.error("error occured while saving user");
		}
		

	}

	public List<User> getAll() {
		return userRepository.findAll();

	}

	public Optional<User> getById(String id) {
		return userRepository.findById(id);
	}

	public void deleteById(String id) {
		userRepository.deleteById(id);

	}

	public User findByUserName(String userName) {
		return userRepository.findByUserName(userName);
	}

}