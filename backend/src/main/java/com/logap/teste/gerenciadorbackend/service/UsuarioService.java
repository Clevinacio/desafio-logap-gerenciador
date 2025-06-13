package com.logap.teste.gerenciadorbackend.service;

import com.logap.teste.gerenciadorbackend.dto.request.UsuarioCreateRequest;
import com.logap.teste.gerenciadorbackend.dto.response.UsuarioResponse;
import com.logap.teste.gerenciadorbackend.dto.request.UsuarioUpdateRequest;

import java.util.List;

public interface UsuarioService {
    UsuarioResponse criarUsuario(UsuarioCreateRequest request);
    List<UsuarioResponse> listarUsuarios();
    UsuarioResponse atualizarRoleUsuario(Long idUsuario, UsuarioUpdateRequest request, String adminEmail);
    void deletarUsuario(Long idUsuario, String adminEmail);
}
