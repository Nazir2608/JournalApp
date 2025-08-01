package net.engineering.journalApp.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Document(collection = "journal_entries")
@Data
public class JournalEntry {

    @Id
    private String id;

    @NotBlank(message = "Title is required") 
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private LocalDateTime date;
    
}
