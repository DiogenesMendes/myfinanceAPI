package com.diogeMendes.personalFinance.service;

import com.diogeMendes.personalFinance.exception.AuthenticationException;
import com.diogeMendes.personalFinance.exception.BusinessExeception;
import com.diogeMendes.personalFinance.model.entity.User;
import com.diogeMendes.personalFinance.model.repository.UserRepository;
import com.diogeMendes.personalFinance.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class UserServiceTest {

    @SpyBean
    UserServiceImpl service;

    @MockBean
    UserRepository repository;


    @Test
    public void mustValidateEmailTest(){
        //given
        User user = createUser();
        // when
        Mockito.when(repository.existsByEmail(user.getEmail())).thenReturn(false);

        //then
        Assertions.assertDoesNotThrow( () -> service.validateEmail(user.getEmail()));

    }

    @Test
    public void mustReturnErroWhenEmailExistsTest(){
        //given
        User user = createUser();

        //when
        Mockito.when(repository.existsByEmail(user.getEmail())).thenReturn(true);

        //then
        org.assertj.core.api.Assertions.catchThrowable( () ->
                service.validateEmail(user.getEmail()));
    }

    @Test
    public void mustAuthenticateUserTest(){
        //given
        User user = createUser();

        //when
        Mockito.when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        //then
        Assertions.assertDoesNotThrow( () -> service.authenticate(user.getEmail(),user.getPassword()));
    }
    @Test
    public void mustThrowExceptionWhenAuthenticateUserDoesNotExistTest(){
        //given
        User user = createUser();
        Mockito.when(repository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        //when
        Throwable execption = catchThrowable( () ->
                                    service.authenticate(user.getEmail(),user.getPassword()));

        //then
        assertThat(execption)
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("User not found!");
    }
    @Test
    public void mustThrowExceptionWhenAuthenticateUserWithInvalidPassworTest(){
        //given
        User user = createUser();
        Mockito.when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        //when
        Throwable execption = catchThrowable( () ->
                service.authenticate(user.getEmail(),"54321"));

        //then
        assertThat(execption)
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("invalid password");
    }

    @Test
    public void mustSaveUserTest(){
        //given
        Mockito.doNothing().when(service).validateEmail(Mockito.anyString());
        User user = createUser();
        user.setId(11L);
        Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(user);

        //when
        User saveUser = service.saveUser(new User());

        //then
        assertThat(saveUser).isNotNull();
        assertThat(saveUser.getId()).isEqualTo(11L);
        assertThat(saveUser.getName()).isEqualTo(user.getName());
        assertThat(saveUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(saveUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void mustDoNotSaveUserWhitDuplicateEmailTest(){
        //given
        User user = createUser();
        Mockito.when(repository.existsByEmail(user.getEmail())).thenReturn(true);

        //when
        Throwable exception = catchThrowable( () -> service.saveUser(user));

        //then
       assertThat(exception)
               .isInstanceOf(BusinessExeception.class)
               .hasMessage("there is already a user with this registered email");
       Mockito.verify(repository,Mockito.never()).save(user);
    }

    public static User createUser(){
        return User.builder().name("Dioge")
                .email("diogenes.mendes01@gmail.com")
                .password("12345").build();
    }
}
