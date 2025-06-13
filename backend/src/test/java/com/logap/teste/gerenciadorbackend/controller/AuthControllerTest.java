package com.logap.teste.gerenciadorbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logap.teste.gerenciadorbackend.configuration.SecurityConfiguration;
import com.logap.teste.gerenciadorbackend.dto.request.LoginRequest;
import com.logap.teste.gerenciadorbackend.repository.UsuarioRepository;
import com.logap.teste.gerenciadorbackend.service.JwtService;
import com.logap.teste.gerenciadorbackend.service.UserDetailServiceImpl;
import com.logap.teste.gerenciadorbackend.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfiguration.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserDetailServiceImpl userDetailService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRealizarLoginERetornarToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        UserDetails userDetails = new User(loginRequest.email(), loginRequest.senha(), new ArrayList<>());
        String fakeToken = "fake-jwt-token";

        when(userDetailService.loadUserByUsername(loginRequest.email())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(fakeToken);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(fakeToken));
    }

    @Test
    void deveRetornarUnathorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest("wrong@example.com", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
