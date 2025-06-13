package com.logap.teste.gerenciadorbackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logap.teste.gerenciadorbackend.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
   Optional<Usuario> findByEmail(String email);

}
