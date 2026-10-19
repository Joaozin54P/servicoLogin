package com.example.servicologin.security;

import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.servicologin.entity.Perfil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// emite o token (porta TokenEmissor do domain) e tambem valida, para proteger /auth/usuarios.
// O perfil vai dentro do token (claim "role"): os outros servicos descobrem se o usuario
// e CLIENTE, COZINHA ou ADMIN sem precisar perguntar para o servicoLogin.
@Component
public class JwtService implements TokenEmissor {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Override
    public String emitir(Long id, String email, String nome, Perfil perfil) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(String.valueOf(id))
                .claim("email", email)
                .claim("role", perfil.name())
                .claim("nome", nome)
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(getSigningKey())
                .compact();
    }

    public Optional<UsuarioToken> ler(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Optional.of(new UsuarioToken(claims.getSubject(), claims.get("role", String.class)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
