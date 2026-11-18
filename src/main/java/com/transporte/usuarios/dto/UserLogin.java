package com.transporte.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserLogin implements Serializable {
    @NotBlank(message = "El campo username no debe estar vacío.")
    @Size(min = 1, max = 15)
    private String username;
    @NotBlank(message = "El campo password no debe estar vacío.")
    @Size(min = 1, max = 15)
    private String password;
}
