package com.example.spring.enums;

public enum Roles {
    BUYER,
    SELLER;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
