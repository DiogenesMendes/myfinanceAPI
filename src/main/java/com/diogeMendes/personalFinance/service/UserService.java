package com.diogeMendes.personalFinance.service;

import com.diogeMendes.personalFinance.model.entity.User;

public interface UserService {

    User authenticate( String email, String password);
    User saveUser(User user);
    void validateEmail(String email);
}
