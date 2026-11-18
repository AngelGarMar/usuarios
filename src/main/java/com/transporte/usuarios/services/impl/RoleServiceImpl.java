package com.transporte.usuarios.services.impl;

import com.transporte.usuarios.constants.Constants;
import com.transporte.usuarios.dto.RoleRequest;
import com.transporte.usuarios.dto.RoleResponse;
import com.transporte.usuarios.entities.Role;
import com.transporte.usuarios.models.ResponseService;
import com.transporte.usuarios.repositories.RoleRepository;
import com.transporte.usuarios.services.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public ResponseService createRole(RoleRequest request) {
        log.info("se revisa si existe el rol");
        Optional<Role> roleOptional = roleRepository.findByRoleAndEstatus(request.getRole().trim().toLowerCase(), 1);
        if (roleOptional.isPresent()) {
            log.info("el rol ya existe");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.ROLE_EXISTS, null);
        }
        log.info("se comienza a guardar el rol");
        Role role = Role.builder()
                .role(request.getRole().trim().toLowerCase())
                .estatus(1)
                .creado(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Mexico_City"))))
                .build();
        RoleResponse response = new RoleResponse(roleRepository.save(role));
        log.info("se regresa el rol guardado");
        return new ResponseService(Constants.RESPONSE_TYPE_SUCCESS, Constants.ROLE_SAVED, response);
    }
}
