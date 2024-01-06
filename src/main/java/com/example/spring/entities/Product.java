package com.example.spring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer amountAvailable;
    private Integer cost;

    @OneToOne
    @JsonIgnore
    private User seller;

    public Product(String name, Integer amountAvailable, Integer cost) {
        this.name = name;
        this.amountAvailable = amountAvailable;
        this.cost = cost;
    }
}
