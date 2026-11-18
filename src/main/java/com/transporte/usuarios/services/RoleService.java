package com.transporte.usuarios.services;

import com.transporte.usuarios.dto.RoleRequest;
import com.transporte.usuarios.models.ResponseService;

public interface RoleService {
    ResponseService createRole(RoleRequest request);
}
