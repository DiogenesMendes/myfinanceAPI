package com.diogeMendes.personalFinance.api.resource;

import com.diogeMendes.personalFinance.api.dto.UserDTO;
import com.diogeMendes.personalFinance.exception.AuthenticationException;
import com.diogeMendes.personalFinance.exception.BusinessExeception;
import com.diogeMendes.personalFinance.model.entity.User;
import com.diogeMendes.personalFinance.service.EntriesService;
import com.diogeMendes.personalFinance.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class UserControllerTest {

    static final String API = "/api/user";
    static final MediaType JSON = MediaType.APPLICATION_JSON;


    @Autowired
    MockMvc mvc;

    @MockBean
    UserService service;

    @MockBean
    EntriesService entriesService;

    @Test
    public void mustAuthenticateUser() throws Exception {
        //given
        String email = "usuario@email.com";
        String senha = "123";

        UserDTO dto = UserDTO.builder().email(email).password(senha).build();
        User user = User.builder().id(1l).email(email).password(senha).build();

        //when
        Mockito.when( service.authenticate(email, senha) ).thenReturn(user);
        String json = new ObjectMapper().writeValueAsString(dto);

        //then
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post( API.concat("/authenticate") )
                .accept( JSON )
                .contentType( JSON )
                .content(json);


        mvc
                .perform(request)
                .andExpect( MockMvcResultMatchers.status().isOk()  )
                .andExpect( MockMvcResultMatchers.jsonPath("id").value(user.getId())  )
                .andExpect( MockMvcResultMatchers.jsonPath("name").value(user.getName())  )
                .andExpect( MockMvcResultMatchers.jsonPath("email").value(user.getEmail())  )

        ;

    }

    @Test
    public void mustReturnBadRequestWhenTryAuthenticate() throws Exception {
        //given
        String email = "usuario@email.com";
        String senha = "123";

        UserDTO dto = UserDTO.builder().email(email).password(senha).build();

        //when
        Mockito.when( service.authenticate(email, senha) ).thenThrow(AuthenticationException.class);
        String json = new ObjectMapper().writeValueAsString(dto);

        //then
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post( API.concat("/authenticate") )
                .accept( JSON )
                .contentType( JSON )
                .content(json);


        mvc
                .perform(request)
                .andExpect( MockMvcResultMatchers.status().isBadRequest());

        ;

    }
    @Test
    public void mustSaveUser() throws Exception {
        //given
        String email = "usuario@email.com";
        String senha = "123";

        UserDTO dto = UserDTO.builder().email("usuario@email.com").password("123").build();
        User user = User.builder().id(1l).email(email).password(senha).build();

        //when
        Mockito.when( service.saveUser(Mockito.any(User.class)) ).thenReturn(user);
        String json = new ObjectMapper().writeValueAsString(dto);

        //then
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post( API  )
                .accept( JSON )
                .contentType( JSON )
                .content(json);


        mvc
                .perform(request)
                .andExpect( MockMvcResultMatchers.status().isCreated()  )
                .andExpect( MockMvcResultMatchers.jsonPath("id").value(user.getId())  )
                .andExpect( MockMvcResultMatchers.jsonPath("name").value(user.getName())  )
                .andExpect( MockMvcResultMatchers.jsonPath("email").value(user.getEmail())  )

        ;

    }

    @Test
    public void mustReturnBadRequestTryCreateUserInvalid() throws Exception {
        //given
        UserDTO dto = UserDTO.builder().email("usuario@email.com").password("123").build();

        //when
        Mockito.when( service.saveUser(Mockito.any(User.class)) ).thenThrow(BusinessExeception.class);
        String json = new ObjectMapper().writeValueAsString(dto);

        //then
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post( API  )
                .accept( JSON )
                .contentType( JSON )
                .content(json);


        mvc
                .perform(request)
                .andExpect( MockMvcResultMatchers.status().isBadRequest()  );

        ;

    }

    @Test
    public void mustGetBalanceOfTherUser() throws Exception {

        //given

        BigDecimal saldo = BigDecimal.valueOf(10);
        User user = User.builder().id(1l).email("usuario@email.com").password( "123").build();

        //when
        Mockito.when(service.getUserById(1l)).thenReturn(Optional.of(user));
        Mockito.when(entriesService.getBalanceByEntriesAndUser(1l)).thenReturn(saldo);


        //then
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get( API.concat("/1/balance")  )
                .accept( JSON )
                .contentType( JSON );
        mvc
                .perform(request)
                .andExpect( MockMvcResultMatchers.status().isOk() )
                .andExpect( MockMvcResultMatchers.content().string("10") );

    }

    @Test
    public void mustReturnResourceNotFoundWhenUserDoesNotExist() throws Exception {

        //given and when
        Mockito.when(service.getUserById(1l)).thenReturn(Optional.empty());


        //then
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get( API.concat("/1/balance")  )
                .accept( JSON )
                .contentType( JSON );
        mvc
                .perform(request)
                .andExpect( MockMvcResultMatchers.status().isNotFound() );

    }

    public static UserDTO createUserDTO(){
        return UserDTO.builder()
                .email("diogenes.mendes01@gmail.com")
                .password("12345").build();
    }
    public static User createUser(){
        return User.builder()
                .email("diogenes.mendes01@gmail.com")
                .password("12345").build();
    }
}
