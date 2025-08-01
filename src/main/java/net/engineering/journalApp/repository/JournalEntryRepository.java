package net.engineering.journalApp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.engineering.journalApp.entity.JournalEntry;

public interface JournalEntryRepository extends MongoRepository<JournalEntry, String> {

}
