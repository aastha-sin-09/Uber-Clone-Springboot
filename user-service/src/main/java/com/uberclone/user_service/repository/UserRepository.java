package com.uberclone.user_service.repository;

import com.uberclone.user_service.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);   // Spring Data generates this query automatically
    boolean existsByEmail(String email);
}