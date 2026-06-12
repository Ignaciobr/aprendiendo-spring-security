package com.app.controller.dto;

import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public record AuthCreateRoleRequest(
        //Un usuario puede tener max 3 roles
        //Mensaje de error
        @Size(max = 3, message = "The user cannot have more than 3 roles")
        List<String> roleListName) {
}
