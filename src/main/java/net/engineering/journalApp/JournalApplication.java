package net.engineering.journalApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@SpringBootApplication
public class JournalApplication {

	private static final Logger logger = LoggerFactory.getLogger(JournalApplication.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(JournalApplication.class, args);
		Environment env = context.getEnvironment();
		logger.info("Application Started on port: " + env.getProperty("server.port"));
		
		try {
			String activeProfiles = env.getActiveProfiles()[0];
			logger.info("Application Started on activeProfiles: " + activeProfiles);
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
            String mongoUri = env.getProperty("spring.data.mongodb.uri");

            if (mongoUri == null || mongoUri.isEmpty()) {
                throw new IllegalStateException("MongoDB URI is missing in application.properties");
            }

            try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
                mongoClient.listDatabaseNames().forEach(db -> logger.info("Database: {}", db));
                logger.info("MongoDB connection successful");
            }
        } catch (Exception e) {
            logger.error("MongoDB connection failed", e);
        }
//		finally {
//            context.close();
//        }
	}
}
