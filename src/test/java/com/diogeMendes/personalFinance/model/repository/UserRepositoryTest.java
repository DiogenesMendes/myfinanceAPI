package com.diogeMendes.personalFinance.model.repository;

import com.diogeMendes.personalFinance.model.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.lang.management.OperatingSystemMXBean;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static  org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTest {

        @Autowired
        TestEntityManager entityManager;


        @Autowired
        UserRepository repository;

        @Test
        public void mustVerifyExistsByEmail() {
            //given
                User user = createUser();
                entityManager.persist(user);

                //when
                boolean result = repository.existsByEmail("diogenes.mendes01@gmail.com");

            //then
                assertThat(result).isTrue();
        }

        @Test
        public void mustReturnFalseWhenEmailDoestExists(){

                //given
                //when
                boolean result = repository.existsByEmail("diogenes.mendes01@gmail.com");

                //then
                assertThat(result).isFalse();

        }
        @Test
        public void mustSaveUser(){
                //given
                User user = createUser();

                //when
                User saveUser  = repository.save(user);

                //then
                assertThat(saveUser.getId()).isNotNull();
        }
        @Test
        public void mustFindUserByEmail(){
                //given
                User user = createUser();
                entityManager.persist(user);

                //when
                Optional<User> result = repository.findByEmail(user.getEmail());

                //then
                assertThat(result.isPresent()).isTrue();
        }
        @Test
        public void mustReturnNullToFindUserByEmail(){
                //given
                User user = createUser();
                entityManager.persist(user);

                //when
                Optional<User> result = repository.findByEmail("paulo.bikes@gmail.com");

                //then
                assertThat(result.isPresent()).isFalse();
        }

        public static User createUser(){
                return User.builder().name("Dioge")
                        .email("diogenes.mendes01@gmail.com")
                        .password("12345").build();
        }
}
