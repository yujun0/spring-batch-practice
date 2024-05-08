package com.example._29_itemprocessor_composite;

public class UserServiceImpl {
    public User toUpperCase(User user) {
        user.setName(user.getName().toUpperCase());
        return user;
    }
}
