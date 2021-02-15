package com.diogeMendes.personalFinance.api.resource;

import com.diogeMendes.personalFinance.api.dto.EntriesDTO;
import com.diogeMendes.personalFinance.api.dto.UpdateStatusDTO;
import com.diogeMendes.personalFinance.exception.BusinessExeception;
import com.diogeMendes.personalFinance.model.entity.Entries;
import com.diogeMendes.personalFinance.model.entity.User;
import com.diogeMendes.personalFinance.model.enums.EntriesStatus;
import com.diogeMendes.personalFinance.model.enums.EntriesType;
import com.diogeMendes.personalFinance.service.EntriesService;
import com.diogeMendes.personalFinance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.platform.commons.function.Try;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/entries")
@RequiredArgsConstructor
public class EntriesController {

    private final EntriesService service;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @PostMapping
    public ResponseEntity create (@Valid @RequestBody EntriesDTO dto){
        Entries entity = modelMapper.map(dto, Entries.class);
        try {
            entity.setUserId(getUserById(entity.getUserId().getId()));
            entity = service.save(entity);
            return new ResponseEntity(modelMapper.map(entity, EntriesDTO.class), HttpStatus.CREATED);
        }catch (BusinessExeception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("{id}")
    public ResponseEntity update (@PathVariable("id") Long id, @RequestBody EntriesDTO dto){
        return service.getById(id).map( entrie -> {
            try {
                Entries entity = modelMapper.map(dto, Entries.class);
                entity.setUserId(getUserById(entrie.getUserId().getId()));
                entity.setId(entrie.getId());
                service.update(entity);
                return new ResponseEntity(modelMapper.map(entity, EntriesDTO.class), HttpStatus.CREATED);
            }catch (BusinessExeception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            } }).orElseGet( () ->
                new ResponseEntity("Entrie not found in the database", HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete( @PathVariable("id") Long id ){
        Entries entries = service.getById(id)
                .orElseThrow( () -> new ResponseStatusException( HttpStatus.NOT_FOUND, "id not found in the database"));;
        service.delete(entries);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    @GetMapping
    public ResponseEntity find( @RequestParam(value = "description", required = false) String description,
                                @RequestParam(value = "mount", required = false) Integer mount,
                                @RequestParam(value = "year",  required = false) Integer year,
                                @RequestParam("userId") Long userId){

        Entries entriesFilter = new Entries();
        entriesFilter.setDescription(description);
        entriesFilter.setMount(mount);
        entriesFilter.setYear(year);
        Optional<User> user = userService.getUserById(userId);
        if(!user.isPresent())
            return ResponseEntity.badRequest().body("it was not possible to perform the query user not found.");
        else{
            entriesFilter.setUserId(user.get());
        }

        List<Entries>  entries = service.find(entriesFilter);

        return ResponseEntity.ok(entries);


    }

    @PutMapping("{id}/update-status/")
    public ResponseEntity updateStatus( @PathVariable("id") Long id ,@RequestBody UpdateStatusDTO dto){
        return service.getById(id).map( entries -> {
            EntriesStatus status =  EntriesStatus.valueOf(dto.getStatus());
            if(status == null) {
                return ResponseEntity
                        .badRequest()
                        .body("it was not possible update the entrie status, send a valid status");
            }
            try {
                entries.setEntriesStatus(status);
                service.update(entries);
                return ResponseEntity.ok(entries);
            }catch (BusinessExeception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet( () ->
             new ResponseEntity("it not possible find entrie in data base", HttpStatus.BAD_REQUEST));
    }
    private User getUserById ( Long id){
        User result = userService.getUserById(id)
                .orElseThrow( () -> new BusinessExeception("User not found by the given ID"));
        return result;
    }
}
