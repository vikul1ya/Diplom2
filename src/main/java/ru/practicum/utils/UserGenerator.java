package ru.practicum.utils;

import ru.practicum.model.User;

import java.util.UUID;

public class UserGenerator {
    public static User uniqueUser() {
        String uniq = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String email = "user_" + uniq + "@example.com";
        String password = "P@ssw0rd" + uniq;
        String name = "User" + uniq;
        return new User(email, password, name);
    }
}


