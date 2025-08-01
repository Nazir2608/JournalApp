package net.engineering.journalApp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.engineering.journalApp.entity.User;

public interface UserRepository extends MongoRepository<User, String> {

	User findByUserName(String userName);

	long deleteByUserName(String userName);


}
