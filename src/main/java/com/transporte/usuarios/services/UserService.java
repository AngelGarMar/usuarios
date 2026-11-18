package com.transporte.usuarios.services;

import com.transporte.usuarios.dto.UserLogin;
import com.transporte.usuarios.dto.UserRequest;
import com.transporte.usuarios.dto.UserUpdateRequest;
import com.transporte.usuarios.models.ResponseService;

public interface UserService {
    ResponseService createUser(UserRequest request);
    ResponseService getUser(Integer idUser);
    ResponseService listUsers();
    ResponseService updateUSer(Integer idUser, UserUpdateRequest request);
    ResponseService deleteUser(Integer idUser);
    ResponseService login(UserLogin login);
}
