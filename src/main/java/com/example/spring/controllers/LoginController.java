package com.example.spring.controllers;

import com.example.spring.entities.Token;
import com.example.spring.entities.User;
import com.example.spring.exceptions.UserErrorException;
import com.example.spring.repositories.UserRepository;
import com.example.spring.requests.LoginRequest;
import com.example.spring.responses.TokenResponse;
import com.example.spring.services.AuthService;
import com.example.spring.util.BCryptUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping(path = "login")
@AllArgsConstructor
public class LoginController {
    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping
    public TokenResponse login(@RequestBody LoginRequest loginRequest) throws NoSuchAlgorithmException, UserErrorException {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UserErrorException("user not found"));

        if (!BCryptUtil.verifyHash(loginRequest.getPassword(), user.getPassword())) {
            throw new UserErrorException("wrong password");
        }

        Token token = authService.generateToken(user);

        return new TokenResponse(token.getToken());
    }
}
