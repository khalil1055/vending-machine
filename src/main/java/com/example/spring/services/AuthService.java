package com.example.spring.services;

import com.example.spring.entities.Token;
import com.example.spring.entities.User;
import com.example.spring.repositories.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final TokenRepository tokenRepository;

    /**
     * Authenticates the token and tries to retrieve its user
     *
     * @param token Token Header
     * @return User the user fetched from the provided token
     */
    public User getUserFromToken(String token) {
        Token t = tokenRepository.findByToken(token)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        return t.getUser();
    }


    /**
     * Generates token string
     * @param user the user to generate token for
     * @return Token the generated token
     * @throws NoSuchAlgorithmException thrown if the hashing algorithm is not found
     */
    public Token generateToken(User user) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update((user.getUsername() + UUID.randomUUID() + LocalDateTime.now()).getBytes());
        String encodedString = Base64.getEncoder().encodeToString(messageDigest.digest());

        Optional<Token> userToken = tokenRepository.findByUser(user);

        Token token;

        //update token if it already exists otherwise create new one
        if (userToken.isPresent()) {
            token = userToken.get();
            token.setToken(encodedString);
        } else {
            token = new Token(encodedString, user);
        }
        tokenRepository.save(token);

        return token;
    }
}
