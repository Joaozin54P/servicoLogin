package com.example.servicologin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.servicologin.controller.adapter.AuthControllerAdapter;
import com.example.servicologin.controller.dto.request.CriarUsuarioRequest;
import com.example.servicologin.controller.dto.request.LoginRequest;
import com.example.servicologin.controller.dto.request.RegistroRequest;
import com.example.servicologin.controller.dto.response.LoginResponse;
import com.example.servicologin.controller.dto.response.UsuarioResponse;
import com.example.servicologin.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // publico: tela "criar conta" do cliente
    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@RequestBody RegistroRequest request) {
        return AuthControllerAdapter.castResponse(
                authService.registrar(request.nome(), request.email(), request.senha()));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return AuthControllerAdapter.castResponse(
                authService.login(request.email(), request.senha()));
    }

    // so ADMIN (regra no SecurityConfig): cria usuario da COZINHA, outro ADMIN, etc.
    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criarUsuario(@RequestBody CriarUsuarioRequest request) {
        return AuthControllerAdapter.castResponse(
                authService.registrarComPerfil(
                        request.nome(),
                        request.email(),
                        request.senha(),
                        AuthControllerAdapter.castPerfil(request.perfil())));
    }
}
