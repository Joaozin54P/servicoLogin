package com.example.servicologin.security;

import com.example.servicologin.entity.Perfil;

// porta: o dominio pede um token, a camada spring decide que e JWT
public interface TokenEmissor {
    String emitir(String email, String nome, Perfil perfil);
}
