package com.diogeMendes.personalFinance.service.impl;

import com.diogeMendes.personalFinance.exception.AuthenticationException;
import com.diogeMendes.personalFinance.exception.BusinessExeception;
import com.diogeMendes.personalFinance.model.entity.User;
import com.diogeMendes.personalFinance.model.repository.UserRepository;
import com.diogeMendes.personalFinance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {


    private UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public User authenticate(String email, String password) {
        Optional<User> user = repository.findByEmail(email);
        if(!user.isPresent()){
            throw new AuthenticationException("User not found!");
        }
        if(!user.get().getPassword().equals(password)){
            throw new AuthenticationException("invalid password");
        }
        return user.get();
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        validateEmail(user.getEmail());
        return repository.save(user);
    }

    @Override
    public void validateEmail(String email) {
        if(repository.existsByEmail(email)){
            throw new BusinessExeception("there is already a user with this registered email");
        }
    }
}
