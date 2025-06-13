package com.logap.teste.gerenciadorbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        String secretKey = "4e6ef8f25a2d4d7a8b8c1e2f3a4b5c6d4e6ef8f25a2d4d7a8b8c1e2f3a4b5c6d";
        long expiration = 3600000;
        ReflectionTestUtils.setField(jwtService, "secret", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", expiration);
    }

    @Test
    void deveGerarTokenEExtrairUsernameCorretamente() {
        // 1. Cenário
        UserDetails userDetails = new User("usuario@teste.com", "senha", new ArrayList<>());

        // 2. Ação
        String token = jwtService.generateToken(userDetails);
        String extractedUsername = jwtService.extractUsername(token);

        // 3. Verificação
        assertNotNull(token);
        assertEquals("usuario@teste.com", extractedUsername);
    }

    @Test
    void deveValidarTokenComSucesso() {
        // 1. Cenário
        UserDetails userDetails = new User("usuario@teste.com", "senha", new ArrayList<>());
        String token = jwtService.generateToken(userDetails);

        // 2. Ação
        boolean isTokenValid = jwtService.isTokenValid(token, userDetails);

        // 3. Verificação
        assertTrue(isTokenValid);
    }

    @Test
    void naoDeveValidarTokenParaUsuarioDiferente() {
        // 1. Cenário
        UserDetails userDetails1 = new User("usuario1@teste.com", "senha", new ArrayList<>());
        UserDetails userDetails2 = new User("usuario2@teste.com", "senha", new ArrayList<>());
        String token = jwtService.generateToken(userDetails1);

        // 2. Ação
        boolean isTokenValid = jwtService.isTokenValid(token, userDetails2);

        // 3. Verificação
        assertFalse(isTokenValid);
    }

}
