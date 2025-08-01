package net.engineering.journalApp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.engineering.journalApp.entity.JournalEntry;
import net.engineering.journalApp.entity.User;
import net.engineering.journalApp.repository.JournalEntryRepository;
import net.engineering.journalApp.repository.UserRepository;

@Service
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(JournalEntry.class);

    @Transactional
    public User saveJournalEntry(JournalEntry journalEntity, String userName) {
        try {
            User user = userService.findByUserName(userName);
            if (user == null) {
                throw new RuntimeException("User not found for username: " + userName);
            }

            if (journalEntity.getDate() == null) {
                journalEntity.setDate(LocalDateTime.now());
            }

            JournalEntry savedEntry = journalEntryRepository.save(journalEntity);
            logger.info("Journal entry saved to DB");

            user.getJournalEntries().add(savedEntry);
            userRepository.save(user);

            return user;

        } catch (Exception e) {
            logger.error("Error saving journal entry for user: {}", userName, e);
            throw new RuntimeException("Could not save journal entry", e);
        }
    }

    public List<JournalEntry> getJournalEntries() {
        try {
            return journalEntryRepository.findAll();
        } catch (Exception e) {
            logger.error("Error retrieving journal entries", e);
            throw new RuntimeException("Could not retrieve journal entries", e);
        }
    }

    public Optional<JournalEntry> getJournalEntryById(String id) {
        try {
            return journalEntryRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error fetching journal entry with ID: {}", id, e);
            throw new RuntimeException("Could not fetch journal entry", e);
        }
    }

    @Transactional
    public void deleteById(String myId, String userName) {
        try {
            User user = userService.findByUserName(userName);
            if (user == null) {
                throw new RuntimeException("User not found for username: " + userName);
            }

            boolean removed = user.getJournalEntries().removeIf(x -> x.getId().equals(myId));

            if (removed) {
                userRepository.save(user);
                journalEntryRepository.deleteById(myId);
                logger.info("Journal entry {} deleted for user {}", myId, userName);
            } else {
                throw new RuntimeException("Journal entry not found for this user");
            }

        } catch (Exception e) {
            logger.error("Error deleting journal entry with ID: {} for user: {}", myId, userName, e);
            throw new RuntimeException("Could not delete journal entry", e);
        }
    }

    public ResponseEntity<JournalEntry> updateById(String myId, JournalEntry updatedEntry, String userName) {
        try {
            Optional<JournalEntry> optionalEntry = journalEntryRepository.findById(myId);

            if (optionalEntry.isPresent()) {
                JournalEntry oldEntry = optionalEntry.get();
                oldEntry.setTitle(updatedEntry.getTitle());
                oldEntry.setContent(updatedEntry.getContent());
                journalEntryRepository.save(oldEntry);
                logger.info("Journal entry {} updated for user {}", myId, userName);
                return new ResponseEntity<>(oldEntry, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            logger.error("Error updating journal entry with ID: {} for user: {}", myId, userName, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
