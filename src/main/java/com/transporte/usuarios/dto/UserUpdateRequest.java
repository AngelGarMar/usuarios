package com.transporte.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateRequest implements Serializable {
    @NotBlank(message = "El campo nombre no debe estar vacío.")
    @Size(min = 1, max = 20)
    private String nombre;
    @Size(min = 0, max = 15)
    private String apaterno;
    @Size(min = 0, max = 15)
    private String amaterno;
}
