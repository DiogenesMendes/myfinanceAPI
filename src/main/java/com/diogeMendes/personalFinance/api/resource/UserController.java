package com.diogeMendes.personalFinance.api.resource;

import com.diogeMendes.personalFinance.api.dto.UserDTO;
import com.diogeMendes.personalFinance.exception.AuthenticationException;
import com.diogeMendes.personalFinance.exception.BusinessExeception;
import com.diogeMendes.personalFinance.model.entity.User;
import com.diogeMendes.personalFinance.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@ControllerAdvice
@RequestMapping("/api/user")
public class UserController {

        public UserService service;
        private ModelMapper modelMapper;

        public UserController ( UserService service, ModelMapper mapper){
                this.service = service;
                this.modelMapper = mapper;
        }

        @PostMapping
        public ResponseEntity create(@RequestBody  @Valid UserDTO dto){
                User entity = modelMapper.map(dto, User.class);
                try {
                        entity = service.saveUser(entity);
                        return new ResponseEntity(modelMapper.map(entity, UserDTO.class), HttpStatus.CREATED);
                }catch (BusinessExeception e){
                        return ResponseEntity.badRequest().body(e.getMessage());
                }
        }

        @GetMapping("/authenticate")
        public ResponseEntity authenticate (@RequestBody UserDTO dto){
                User entity = modelMapper.map(dto, User.class);
                try{
                        User UserAuthenticate = service.authenticate(entity.getEmail(),entity.getPassword());
                        return ResponseEntity.ok(modelMapper.map(UserAuthenticate,UserDTO.class));
                }catch (AuthenticationException e){
                        return ResponseEntity.badRequest().body(e.getMessage());
                }
        }
}
