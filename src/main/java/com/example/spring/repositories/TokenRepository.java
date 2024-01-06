package com.example.spring.repositories;

import com.example.spring.entities.Token;
import com.example.spring.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUser(User user);
    Optional<Token> findByToken(String token);
}
