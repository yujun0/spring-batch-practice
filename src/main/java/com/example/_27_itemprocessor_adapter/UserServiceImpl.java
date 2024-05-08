package com.example._27_itemprocessor_adapter;

public class UserServiceImpl {
    public User toUpperCase(User user) {
        user.setName(user.getName().toUpperCase());
        return user;
    }
}
