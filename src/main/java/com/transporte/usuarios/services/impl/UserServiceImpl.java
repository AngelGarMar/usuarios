package com.transporte.usuarios.services.impl;

import com.transporte.usuarios.constants.Constants;
import com.transporte.usuarios.dto.UserLogin;
import com.transporte.usuarios.dto.UserRequest;
import com.transporte.usuarios.dto.UserResponse;
import com.transporte.usuarios.dto.UserUpdateRequest;
import com.transporte.usuarios.entities.Role;
import com.transporte.usuarios.entities.User;
import com.transporte.usuarios.models.ResponseService;
import com.transporte.usuarios.repositories.RoleRepository;
import com.transporte.usuarios.repositories.UserRepository;
import com.transporte.usuarios.services.UserService;
import com.transporte.usuarios.services.auth.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @Value("${app.maintenance.createUser}")
    private boolean createUser;

    @Override
    @Transactional
    public ResponseService createUser(UserRequest request) {
        log.info("se revisa si por configuración se permite crear usuarios");
        if (!createUser) {
            log.info("no se pueden crear usuarios");
            return new ResponseService(Constants.RESPONSE_TYPE_WARNING, Constants.USER_DISABLED, null);
        }
        log.info("se revisa que no exista el username");
        Optional<User> userOptional = userRepository.findByUsernameAndEstatus(request.getUsername().trim(), 1);
        if (userOptional.isPresent()) {
            log.info("el username ya existe");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.USERNAME_EXISTS, null);
        }
        log.info("se obtiene el rol user para guardarlo con el usuario");
        Optional<Role> roleUser = roleRepository.findByRole("user");
        List<Role> roles = new ArrayList<>();
        roleUser.ifPresent(roles::add);
        if (request.getAdmin()) {
            log.info("se obtiene el rol admin para guardarlo con el usuario");
            Optional<Role> roleAdmin = roleRepository.findByRole("admin");
            if (roleAdmin.isEmpty()) {
                log.info("el rol admin no existe");
                return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.ROLE_ADMIN_UNKNOWN, null);
            }
            roleAdmin.ifPresent(roles::add);
        }
        log.info("se comienza a guardar el usuario");
        User user = User.builder()
                .nombre(request.getNombre().trim())
                .apaterno(request.getApaterno() == null ? "" : request.getApaterno().trim())
                .amaterno(request.getAmaterno() == null ? "" : request.getAmaterno().trim())
                .username(request.getUsername().trim())
                .password(passwordEncoder.encode(request.getPassword().trim()))
                .estatus(1)
                .creado(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Mexico_City"))))
                .roles(roles)
                .build();
        UserResponse userResponse = new UserResponse(userRepository.save(user));
        log.info("se regresa el usuario guardado");
        return new ResponseService(Constants.RESPONSE_TYPE_SUCCESS, Constants.USERNAME_SAVED, userResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseService getUser(Integer idUser) {
        log.info("se revisa si existe el usuario");
        Optional<User> userOptional = userRepository.findByIdAndEstatus(idUser, 1);
        if (userOptional.isEmpty()) {
            log.info("el usuario no existe o está dado de baja");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.USERID_UNKNOWN, null);
        }
        log.info("se regresa el usuario obtenido");
        return new ResponseService(Constants.RESPONSE_TYPE_SUCCESS, Constants.USER_OBTAINED, new UserResponse(userOptional.get()));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseService listUsers() {
        log.info("se obtienen todos los usuarios");
        List<User> users = userRepository.findAllByEstatus(1);
        List<UserResponse> usersResponse = new ArrayList<>();
        users.forEach(user -> {
            usersResponse.add(new UserResponse(user));
        });
        log.info("se regresan los {} usuarios", users.size());
        return new ResponseService(Constants.RESPONSE_TYPE_SUCCESS, Constants.USERS_OBTAINED, usersResponse);
    }

    @Override
    @Transactional
    public ResponseService updateUSer(Integer idUser, UserUpdateRequest request) {
        log.info("se revisa si existe el usuario");
        Optional<User> userOptional = userRepository.findByIdAndEstatus(idUser, 1);
        if (userOptional.isEmpty()) {
            log.info("el usuario no existe o está dado de baja");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.USERID_UNKNOWN, null);
        }
        User user = userOptional.get();
        log.info("se modifican los datos");
        user.setNombre(request.getNombre().trim());
        user.setApaterno(request.getApaterno() == null ? "" : request.getApaterno().trim());
        user.setAmaterno(request.getAmaterno() == null ? "" : request.getAmaterno().trim());
        user.setActualizado(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Mexico_City"))));
        log.info("se guarda el usuario");
        UserResponse response = new UserResponse(userRepository.save(user));
        log.info("se regresa el usuario guardado");
        return new ResponseService(Constants.RESPONSE_TYPE_SUCCESS, Constants.USER_MODIFIED, response);
    }

    @Override
    @Transactional
    public ResponseService deleteUser(Integer idUser) {
        log.info("se revisa si existe el usuario");
        Optional<User> userOptional = userRepository.findByIdAndEstatus(idUser, 1);
        if (userOptional.isEmpty()) {
            log.info("el usuario no existe o está dado de baja");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.USERID_UNKNOWN, null);
        }
        User user = userOptional.get();
        log.info("se cambia el estatus a cero");
        user.setEstatus(0);
        user.setActualizado(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Mexico_City"))));
        UserResponse response = new UserResponse(userRepository.save(user));
        return new ResponseService(Constants.RESPONSE_TYPE_SUCCESS, Constants.USER_MODIFIED, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseService login(UserLogin login) {
        log.info("se revisa si existe el usuario");
        Optional<User> userOptional = userRepository.findByUsername(login.getUsername().trim());
        if (userOptional.isEmpty()) {
            log.info("no existe el usuario");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.INCORRECT_LOGIN, null);
        }
        User user = userOptional.get();
        log.info("se revisa la contraseña");
        boolean esCorrecta = passwordEncoder.matches(login.getPassword().trim(), user.getPassword());
        if (!esCorrecta) {
            log.info("la contraseña no coincide");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.INCORRECT_LOGIN, null);
        }
        log.info("se revisa que el usuario esté habilitado");
        if (user.getEstatus() != 1) {
            log.info("el usuario está deshabilitado");
            return new ResponseService(Constants.RESPONSE_TYPE_ERROR, Constants.INCORRECT_LOGIN, null);
        }
        log.info("se comienza a crear el jwt");
        String jwt = jwtService.generateToken(user, generateExtraClaims(user));
        log.info("se regresa el jwt");
        return new ResponseService(Constants.RESPONSE_TYPE_SUCCESS, Constants.CORRECT_LOGIN, jwt);
    }

    private Map<String, Object> generateExtraClaims(User user) {
        List<String> roles = user.getRoles().stream().map(Role::getRole).toList(); //Se crea una lista de roles
        Map<String, Object> extraClaims = new HashMap<>(); //Se generan los claims en base a los roles
        extraClaims.put("authorities", new ObjectMapper().writeValueAsString(roles));
        return extraClaims;
    }
}
