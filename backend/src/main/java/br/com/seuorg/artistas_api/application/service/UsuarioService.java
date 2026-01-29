package br.com.seuorg.artistas_api.application.service;

import br.com.seuorg.artistas_api.application.dto.LoginRequestDTO;
import br.com.seuorg.artistas_api.application.dto.UsuarioCreateDTO;
import br.com.seuorg.artistas_api.domain.entity.Usuario;
import br.com.seuorg.artistas_api.domain.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario criar(UsuarioCreateDTO dto) {
        Optional<Usuario> existe = repository.findByEmail(dto.getEmail());
        if (existe.isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        return repository.save(usuario);
    }

    public Usuario autenticar(LoginRequestDTO dto) {
        Usuario usuario = repository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));
        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }
        return usuario;
    }
}
