package com.transporte.usuarios.controllers;

import com.transporte.usuarios.constants.Constants;
import com.transporte.usuarios.dto.UserLogin;
import com.transporte.usuarios.dto.UserRequest;
import com.transporte.usuarios.dto.UserUpdateRequest;
import com.transporte.usuarios.models.ResponseService;
import com.transporte.usuarios.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService service;

    @PostMapping
    public ResponseEntity<ResponseService> createUser(@RequestBody @Valid UserRequest request, BindingResult result) {
        if (result.hasFieldErrors()) return validation(result);
        ResponseService response = service.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseService> getUser(@PathVariable Integer userId) {
        ResponseService response = service.getUser(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ResponseService> listUsers() {
        ResponseService response = service.listUsers();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseService> deleteUser(@PathVariable Integer userId) {
        ResponseService response = service.deleteUser(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ResponseService> updateUser(@RequestBody @Valid UserUpdateRequest request, BindingResult result, @PathVariable Integer userId) {
        if (result.hasFieldErrors()) return validation(result);
        ResponseService response = service.updateUSer(userId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseService> login(@RequestBody @Valid UserLogin login, BindingResult result) {
        if (result.hasFieldErrors()) return validation(result);
        ResponseService response = service.login(login);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ResponseEntity<ResponseService> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), err.getDefaultMessage());
        });
        return new ResponseEntity<>(new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.MISSED_INFORMATION, errors), HttpStatus.OK);
    }
}
