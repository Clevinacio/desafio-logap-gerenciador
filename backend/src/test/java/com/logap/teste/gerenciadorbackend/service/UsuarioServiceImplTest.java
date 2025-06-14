package com.logap.teste.gerenciadorbackend.service;

import com.logap.teste.gerenciadorbackend.dto.request.UsuarioCreateRequest;
import com.logap.teste.gerenciadorbackend.dto.response.UsuarioResponse;
import com.logap.teste.gerenciadorbackend.dto.request.UsuarioUpdateRequest;
import com.logap.teste.gerenciadorbackend.exception.BusinessException;
import com.logap.teste.gerenciadorbackend.model.Usuario;
import com.logap.teste.gerenciadorbackend.model.enums.Perfil;
import com.logap.teste.gerenciadorbackend.repository.PedidoRepository;
import com.logap.teste.gerenciadorbackend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceImplTest {

    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private UsuarioServiceImpl usuarioService;
    private PedidoRepository pedidoRepository;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        pedidoRepository = mock(PedidoRepository.class);
        usuarioService = new UsuarioServiceImpl(usuarioRepository, passwordEncoder, pedidoRepository);
    }

    @Test
    void deveCriarUsuarioComSucesso() {
        UsuarioCreateRequest request = new UsuarioCreateRequest(
                "Fulano",
                "fulano@email,com",
                "senha123",
                Perfil.CLIENTE
        );

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.senha())).thenReturn("senhaCriptografada");
        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(1L);
        usuarioSalvo.setNome(request.nome());
        usuarioSalvo.setEmail(request.email());
        usuarioSalvo.setSenha("senhaCriptografada");
        usuarioSalvo.setPerfil(Perfil.CLIENTE);

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        UsuarioResponse usuarioCriado = usuarioService.criarUsuario(request);

        assertNotNull(usuarioCriado);
        assertEquals(request.nome(), usuarioCriado.nome());
        assertEquals(request.email(), usuarioCriado.email());
        assertEquals(Perfil.CLIENTE, usuarioCriado.perfil());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario usuarioParaSalvar = captor.getValue();
        assertEquals(request.nome(), usuarioParaSalvar.getNome());
        assertEquals(request.email(), usuarioParaSalvar.getEmail());
        assertEquals("senhaCriptografada", usuarioParaSalvar.getSenha());
        assertEquals(Perfil.CLIENTE, usuarioParaSalvar.getPerfil());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        UsuarioCreateRequest request = new UsuarioCreateRequest(
                "Fulano",
                "fulano@email.com",
                "senha123",
                Perfil.CLIENTE
        );

        when(usuarioRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(new Usuario()));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                usuarioService.criarUsuario(request)
        );

        assertEquals("Email já cadastrado.", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveListarUsuariosComSucesso() {
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setNome("Fulano");
        usuario1.setEmail("fulano@email.com");
        usuario1.setPerfil(Perfil.CLIENTE);
        usuario1.setDataCriacao(Instant.now());

        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNome("Beltrano");
        usuario2.setEmail("beltrano@email.com");
        usuario2.setPerfil(Perfil.ADMINISTRADOR);
        usuario2.setDataCriacao(Instant.now());

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario1, usuario2));

        List<UsuarioResponse> usuarios = usuarioService.listarUsuarios();

        assertEquals(2, usuarios.size());
        assertEquals("Fulano", usuarios.get(0).nome());
        assertEquals("Beltrano", usuarios.get(1).nome());
    }

    @Test
    void deveAtualizarRoleUsuarioComSucesso() {
        Long idUsuario = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        usuario.setNome("Fulano");
        usuario.setEmail("fulano@email.com");
        usuario.setPerfil(Perfil.CLIENTE);

        UsuarioUpdateRequest updateRequest = new UsuarioUpdateRequest(Perfil.ADMINISTRADOR);

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponse response = usuarioService.atualizarRoleUsuario(idUsuario, updateRequest, "admin@email.com");

        assertNotNull(response);
        assertEquals(Perfil.ADMINISTRADOR, response.perfil());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarExcecaoAoAtualizarRoleUsuarioQuandoUsuarioNaoExiste() {
        Long idUsuario = 99L;
        UsuarioUpdateRequest updateRequest = new UsuarioUpdateRequest(Perfil.ADMINISTRADOR);

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                usuarioService.atualizarRoleUsuario(idUsuario, updateRequest, "admin@email.com")
        );

        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoAdminTentaAlterarProprioPerfil() {
        Long idUsuario = 1L;
        String adminEmail = "admin@email.com";

        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        usuario.setNome("Admin");
        usuario.setEmail(adminEmail);
        usuario.setPerfil(Perfil.ADMINISTRADOR);

        UsuarioUpdateRequest updateRequest = new UsuarioUpdateRequest(Perfil.CLIENTE);

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                usuarioService.atualizarRoleUsuario(idUsuario, updateRequest, adminEmail)
        );

        assertEquals("Você não pode alterar seu próprio perfil de administrador.", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveDeletarUsuarioQuandoUsuarioExisteEAdminNaoEhProprio() {
        Long idUsuario = 1L;
        String adminEmail = "admin@email.com";

        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        usuario.setEmail("usuario@email.com");

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));

        usuarioService.deletarUsuario(idUsuario, adminEmail);

        verify(usuarioRepository).deleteById(idUsuario);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExisteAoDeletar() {
        Long idUsuario = 99L;
        String adminEmail = "admin@email.com";

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                usuarioService.deletarUsuario(idUsuario, adminEmail)
        );

        assertEquals("Usuário não encontrado com ID: " + idUsuario, exception.getMessage());
        verify(usuarioRepository, never()).deleteById(any());
    }

    @Test
    void deveLancarExcecaoQuandoAdminTentaExcluirPropriaConta() {
        Long idUsuario = 1L;
        String adminEmail = "admin@email.com";

        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        usuario.setEmail(adminEmail);

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                usuarioService.deletarUsuario(idUsuario, adminEmail)
        );

        assertEquals("Você não pode excluir sua própria conta de administrador.", exception.getMessage());
        verify(usuarioRepository, never()).deleteById(any());
    }

    @Test
    void naoDeveDeletarUsuarioComPedidosAssociados() {
        Long idUsuario = 1L;
        String adminEmail = "admin@teste.com";
        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        usuario.setEmail(adminEmail);

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(pedidoRepository.existsByClienteId(idUsuario)).thenReturn(true);
        BusinessException exception = assertThrows(BusinessException.class, () ->
                usuarioService.deletarUsuario(idUsuario, adminEmail)
        );
        assertEquals("Não é possível excluir um usuário que possui pedidos associados.", exception.getMessage());
        verify(usuarioRepository, never()).deleteById(any());
}

}