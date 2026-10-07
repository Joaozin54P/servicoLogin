package com.example.servicologin.controller.dto.response;

public record LoginResponse(
        String token,
        String nome,
        String perfil
) {
}
