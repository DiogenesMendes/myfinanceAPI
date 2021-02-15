package com.diogeMendes.personalFinance.model.repository;

import com.diogeMendes.personalFinance.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository <User , Long> {

    Optional<User> findByEmail (String email);
    Optional<User> findByName (String nome);
    boolean existsByEmail(String email);

}
