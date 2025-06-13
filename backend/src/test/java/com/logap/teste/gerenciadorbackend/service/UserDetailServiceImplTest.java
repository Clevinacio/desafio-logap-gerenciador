package com.logap.teste.gerenciadorbackend.service;

import com.logap.teste.gerenciadorbackend.model.Usuario;
import com.logap.teste.gerenciadorbackend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDetailServiceImplTest {

    private UsuarioRepository usuarioRepository;
    private UserDetailServiceImpl userDetailService;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        userDetailService = new UserDetailServiceImpl(usuarioRepository);
    }

    @Test
    void deveRetornarUsuarioQuandoEncontradoPorEmail() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@email.com");
        usuario.setSenha("senha123");

        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(usuario));

        UserDetails result = userDetailService.loadUserByUsername("teste@email.com");

        assertNotNull(result);
        assertEquals("teste@email.com", result.getUsername());
        assertEquals("senha123", result.getPassword());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailService.loadUserByUsername("naoexiste@email.com"));
    }
}
