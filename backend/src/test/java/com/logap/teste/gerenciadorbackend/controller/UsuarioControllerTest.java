package com.logap.teste.gerenciadorbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logap.teste.gerenciadorbackend.configuration.SecurityConfiguration;
import com.logap.teste.gerenciadorbackend.dto.request.UsuarioCreateRequest;
import com.logap.teste.gerenciadorbackend.dto.response.UsuarioResponse;
import com.logap.teste.gerenciadorbackend.dto.request.UsuarioUpdateRequest;
import com.logap.teste.gerenciadorbackend.exception.BusinessException;
import com.logap.teste.gerenciadorbackend.model.enums.Perfil;
import com.logap.teste.gerenciadorbackend.service.JwtService;
import com.logap.teste.gerenciadorbackend.service.UserDetailServiceImpl;
import com.logap.teste.gerenciadorbackend.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@WithMockUser(roles = "ADMINISTRADOR")
@Import(SecurityConfiguration.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailServiceImpl userDetailService;

    @MockitoBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioResponse usuarioResponse;

    @BeforeEach
    void setUp() {
        usuarioResponse = new UsuarioResponse(
                1L,
                "Fulano",
                "fulano@email.com",
                Perfil.CLIENTE,
                null
        );
    }

    @Test
    void deveListarTodosUsuarios() throws Exception {
        List<UsuarioResponse> usuarios = Arrays.asList(
                usuarioResponse,
                new UsuarioResponse(2L, "Beltrano", "beltrano@email.com", Perfil.ADMINISTRADOR, null)
        );
        when(usuarioService.listarUsuarios()).thenReturn(usuarios);

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome", is("Fulano")))
                .andExpect(jsonPath("$[1].nome", is("Beltrano")));
    }

    @Test
    void deveCriarUsuario() throws Exception {
        UsuarioCreateRequest request = new UsuarioCreateRequest("Fulano", "fulano@email.com", "senha123", Perfil.CLIENTE);
        when(usuarioService.criarUsuario(any(UsuarioCreateRequest.class))).thenReturn(usuarioResponse);

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Fulano")))
                .andExpect(jsonPath("$.email", is("fulano@email.com")));
    }

    @Test
    void deveAtualizarPerfilUsuario() throws Exception {
        UsuarioUpdateRequest updateRequest = new UsuarioUpdateRequest(Perfil.ADMINISTRADOR);
        UsuarioResponse atualizado = new UsuarioResponse(1L, "Fulano", "fulano@email.com", Perfil.ADMINISTRADOR, null);

        when(usuarioService.atualizarRoleUsuario(eq(1L), any(UsuarioUpdateRequest.class), eq("admin@email.com"))).thenReturn(atualizado);
        mockMvc.perform(patch("/usuarios/1/perfil")
                        .with(user("admin@email.com").roles("ADMINISTRADOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfil", is("ADMINISTRADOR")));
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void deveRetornar403ParaUsuarioNaoAutorizado() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar403QuandoUsuarioNaoAutorizadoTentaAtualizarPerfil() throws Exception {
        UsuarioUpdateRequest updateRequest = new UsuarioUpdateRequest(Perfil.ADMINISTRADOR);


        mockMvc.perform(patch("/usuarios/1/perfil")
                        .with(user("vendedor@email.com").roles("VENDEDOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

   @Test
   void retorna403QuandoAdminTentaAtualizarProprioPerfil() throws Exception {
       UsuarioUpdateRequest updateRequest = new UsuarioUpdateRequest(Perfil.CLIENTE);
         doThrow(new BusinessException("Não é permitido atualizar o próprio perfil"))
                .when(usuarioService).atualizarRoleUsuario(eq(1L), any(UsuarioUpdateRequest.class), eq("admin@email.com"));

       mockMvc.perform(patch("/usuarios/1/perfil")
                       .with(user("admin@email.com").roles("ADMINISTRADOR"))
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(updateRequest)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message", is("Não é permitido atualizar o próprio perfil")));
   }

    @Test
    void deveDeletarUsuarioComSucesso() throws Exception {
        doNothing().when(usuarioService).deletarUsuario(eq(1L), eq("admin@email.com"));

        mockMvc.perform(delete("/usuarios/1")
                        .with(user("admin@email.com").roles("ADMINISTRADOR")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar403AoDeletarUsuarioSemAutorizacao() throws Exception {
        mockMvc.perform(delete("/usuarios/1")
                        .with(user("vendedor@email.com").roles("VENDEDOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar403AoDeletarUsuarioInexistente() throws Exception {
        doThrow(new BusinessException("Usuário não encontrado"))
                .when(usuarioService).deletarUsuario(eq(999L), eq("admin@email.com"));

        mockMvc.perform(delete("/usuarios/999")
                        .with(user("admin@email.com").roles("ADMINISTRADOR")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Usuário não encontrado")));
    }

    @Test
    void deveRetornar403AoDeletarProprioUsuario() throws Exception {
        doThrow(new BusinessException("Não é permitido deletar o próprio usuário"))
                .when(usuarioService).deletarUsuario(eq(1L), eq("admin@email.com"));
        mockMvc.perform(delete("/usuarios/1")
                        .with(user("admin@email.com").roles("ADMINISTRADOR")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Não é permitido deletar o próprio usuário")));
    }
}
