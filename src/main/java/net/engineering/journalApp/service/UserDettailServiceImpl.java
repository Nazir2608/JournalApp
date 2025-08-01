package net.engineering.journalApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import net.engineering.journalApp.entity.User;
import net.engineering.journalApp.repository.UserRepository;

@Component
public class UserDettailServiceImpl implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		User user = userRepository.findByUserName(userName);
		if (user != null) {
			UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
					.username(user.getUserName()).password(user.getPassword())
					.roles(user.getRoles().toArray(new String[0])).build();
			return userDetails;
		}
		throw new UsernameNotFoundException("User not found with userName: " + userName);
	}

}
