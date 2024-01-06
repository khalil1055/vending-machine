package com.example.spring.controllers;

import com.example.spring.entities.User;
import com.example.spring.enums.Privileges;
import com.example.spring.requests.BuyProductRequest;
import com.example.spring.requests.CreateUserRequest;
import com.example.spring.requests.UserDepositRequest;
import com.example.spring.responses.RoleResponse;
import com.example.spring.responses.UserResponse;
import com.example.spring.services.AuthService;
import com.example.spring.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping(path = "roles")
    public List<RoleResponse> getUserRoles(
            @RequestHeader("token") String token
    ) {
        User user = authService.getUserFromToken(token);

        return userService.getUserRoles(user);
    }

    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest user) {
        User u = userService.createUser(user);

        return new UserResponse(u.getId(), u.getUsername(), u.getDeposit());
    }

    @PostMapping(path = "roles/{roleId}")
    public UserResponse addRole(
            @RequestHeader("token") String token,
            @PathVariable Long roleId
    ) {
        User user = authService.getUserFromToken(token);
        userService.addUserRole(user, roleId);

        return new UserResponse(user.getId(), user.getUsername(), user.getDeposit());
    }

    @PostMapping(path = "deposit")
    public UserResponse deposit(
            @RequestHeader("token") String token,
            @RequestBody UserDepositRequest depositRequest
    ) {
        User user = authService.getUserFromToken(token);

        userService.userHasPrivilege(user, Privileges.USER_DEPOSIT);

        User u = userService.depositToUser(user, depositRequest.getAmount());

        return new UserResponse(u.getId(), u.getUsername(), u.getDeposit());
    }

    @PostMapping(path = "buy")
    public String buyProduct(
            @RequestHeader("token") String token,
            @RequestBody BuyProductRequest buyProductRequest
    ) {
        User user = authService.getUserFromToken(token);

        userService.userHasPrivilege(user, Privileges.PRODUCT_BUY);

        return userService.buyProduct(user, buyProductRequest.getProductId(), buyProductRequest.getQuantity());
    }

    @PostMapping(path = "reset")
    public UserResponse resetDeposit(
            @RequestHeader("token") String token
    ) {
        User user = authService.getUserFromToken(token);

        User u = userService.resetUserDeposit(user);

        return new UserResponse(u.getId(), u.getUsername(), u.getDeposit());
    }
}
