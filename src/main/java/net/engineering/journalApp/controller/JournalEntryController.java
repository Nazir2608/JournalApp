package net.engineering.journalApp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import net.engineering.journalApp.entity.JournalEntry;
import net.engineering.journalApp.entity.User;
import net.engineering.journalApp.service.JournalEntryService;
import net.engineering.journalApp.service.UserService;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    // CREATE Journal Entry
    @PostMapping
    public ResponseEntity<?> createEntry(@Valid @RequestBody JournalEntry journalEntity) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = journalEntryService.saveJournalEntry(journalEntity, userName);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create entry: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // GET All Journal Entries of Logged-in User
    @GetMapping
    public ResponseEntity<?> getAllEntriesOfUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);

        List<JournalEntry> journalEntries = user.getJournalEntries();

        if (journalEntries == null || journalEntries.isEmpty()) {
            return ResponseEntity.ok(List.of()); // Returns empty list instead of 404
        }
        return new ResponseEntity<>(journalEntries, HttpStatus.OK);
    }

    // GET Journal Entry by ID for Logged-in User
    @GetMapping("/id/{myId}")
    public ResponseEntity<?> getJournalEntryById(@PathVariable String myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);

        Optional<JournalEntry> entry = user.getJournalEntries()
                                           .stream()
                                           .filter(j -> j.getId().equals(myId))
                                           .findFirst();

        return entry
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Entry not found for this user"));
    }

    // DELETE Journal Entry by ID for Logged-in User
    @DeleteMapping("id/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable String myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try {
            journalEntryService.deleteById(myId, userName);
            return ResponseEntity.ok("Journal entry deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unable to delete entry: " + e.getMessage());
        }
    }

    // UPDATE Journal Entry by ID for Logged-in User
    @PutMapping("id/{myId}")
    public ResponseEntity<?> updateJournalEntryById(@PathVariable String myId,
                                                    @RequestBody JournalEntry updatedEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        ResponseEntity<JournalEntry> responseEntity = journalEntryService.updateById(myId, updatedEntry, userName);
        return new ResponseEntity<>(responseEntity.getBody(), responseEntity.getStatusCode());
    }
}
