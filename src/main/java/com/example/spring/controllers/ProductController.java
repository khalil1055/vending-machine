package com.example.spring.controllers;

import com.example.spring.entities.Product;
import com.example.spring.entities.User;
import com.example.spring.enums.Privileges;
import com.example.spring.requests.CreateProductRequest;
import com.example.spring.requests.UpdateProductRequest;
import com.example.spring.services.AuthService;
import com.example.spring.services.ProductService;
import com.example.spring.services.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/product")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final UserService userService;
    private final AuthService authService;

    Logger logger = LoggerFactory.getLogger(ProductController.class);


    @GetMapping
    public List<Product> get(
            @RequestHeader("token") String token
    ) {
        User user = authService.getUserFromToken(token);

        return productService.getProducts(user);
    }

    @PostMapping
    public Product createProduct(
            @RequestHeader("token") String token,
            @RequestBody CreateProductRequest productRequest
    ) {
        User user = authService.getUserFromToken(token);

        userService.userHasPrivilege(user, Privileges.PRODUCT_ADD);

        Product product = productService.createProduct(
                user,
                productRequest.getName(),
                productRequest.getAmountAvailable(),
                productRequest.getCost()
        );

        logger.info("User " + user.getUsername() + " created " + product.getName());

        return product;
    }

    @DeleteMapping(path = "{productId}")
    public Product deleteProduct(
            @RequestHeader("token") String token,
            @PathVariable Long productId
    ) {
        User user = authService.getUserFromToken(token);

        userService.userHasPrivilege(user, Privileges.PRODUCT_REMOVE);

        return productService.deleteProduct(user, productId);
    }

    @PutMapping(path = "{productId}")
    public Product updateProduct(
            @PathVariable Long productId,
            @RequestHeader("token") String token,
            @RequestBody UpdateProductRequest updateProductRequest
    ) {
        User user = authService.getUserFromToken(token);

        userService.userHasPrivilege(user, Privileges.PRODUCT_UPDATE);

        return productService.updateProduct(user, productId, updateProductRequest);
    }
}
