package com.logap.teste.gerenciadorbackend.service;

import com.logap.teste.gerenciadorbackend.dto.request.UsuarioCreateRequest;
import com.logap.teste.gerenciadorbackend.dto.response.UsuarioResponse;
import com.logap.teste.gerenciadorbackend.dto.request.UsuarioUpdateRequest;
import com.logap.teste.gerenciadorbackend.exception.BusinessException;
import com.logap.teste.gerenciadorbackend.model.Usuario;
import com.logap.teste.gerenciadorbackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    @Override
    public UsuarioResponse criarUsuario(UsuarioCreateRequest request) {
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException("Email já cadastrado.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(request.nome());
        novoUsuario.setEmail(request.email());
        novoUsuario.setSenha(encoder.encode(request.senha()));
        novoUsuario.setPerfil(request.perfil());

        Usuario usuarioCriado = usuarioRepository.save(novoUsuario);
        return mapToResponse(usuarioCriado);
    }

    @Override
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponse atualizarRoleUsuario(Long idUsuario, UsuarioUpdateRequest request, String adminEmail) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com ID: " + idUsuario));

        if (usuario.getEmail().equals(adminEmail)) {
            throw new BusinessException("Você não pode alterar seu próprio perfil de administrador.");
        }

        usuario.setPerfil(request.perfil());
        return mapToResponse(usuarioRepository.save(usuario));
    }

    @Override
    public void deletarUsuario(Long idUsuario, String adminEmail) {
        Usuario usuarioASerDeletado = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com ID: " + idUsuario));

        // Regra de negócio: Impede que um administrador exclua a própria conta
        if (usuarioASerDeletado.getEmail().equals(adminEmail)) {
            throw new BusinessException("Você não pode excluir sua própria conta de administrador.");
        }

        usuarioRepository.deleteById(usuarioASerDeletado.getId());
    }

    private UsuarioResponse mapToResponse(Usuario usuarioCriado) {
        return new UsuarioResponse(
                usuarioCriado.getId(),
                usuarioCriado.getNome(),
                usuarioCriado.getEmail(),
                usuarioCriado.getPerfil(),
                usuarioCriado.getDataCriacao()
        );
    }
}
