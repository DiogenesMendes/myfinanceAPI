package com.diogeMendes.personalFinance.service;

import com.diogeMendes.personalFinance.model.entity.User;

import java.util.Optional;

public interface UserService {

    User authenticate( String email, String password);
    User saveUser(User user);
    void validateEmail(String email);
    Optional<User> getUserById (Long id);
}
