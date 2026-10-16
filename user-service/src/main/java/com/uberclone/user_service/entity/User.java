package com.uberclone.user_service.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Data                          // Lombok: generates getters, setters, toString
@Builder                       // Lombok: enables builder pattern
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")  // MongoDB collection name
public class User {

    @Id
    private String id;           // MongoDB uses String IDs (ObjectId)

    private String name;

    @Indexed(unique = true)      // MongoDB index — fast lookup + no duplicate emails
    private String email;

    private String password;     // Will store hashed password (never plain text)

    private String phone;

    private String role;         // "RIDER" or "DRIVER"

    private LocalDateTime createdAt;
}