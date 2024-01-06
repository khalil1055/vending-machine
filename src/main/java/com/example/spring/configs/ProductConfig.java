package com.example.spring.configs;

import com.example.spring.entities.Product;
import com.example.spring.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class ProductConfig {
    private final ProductRepository productRepository;

    @Bean
    CommandLineRunner productSeeder() {
        return args -> {
            Product product = new Product();
            product.setCost(5);
            product.setName("product 1");
            product.setAmountAvailable(1000);
            product.setSeller(null);

            productRepository.save(product);
        };
    }
}
