package com.app.controller.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

//Este es el orden en el que se van a mostrar en el json
@JsonPropertyOrder({"username", "message", "jwt", "status"})

//Record es para no poner los getter y setter
public record AuthResponse(String username,
                           String message,
                           String jwt,
                           boolean status) {


}
