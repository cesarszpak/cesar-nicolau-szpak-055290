package br.com.seuorg.artistas_api.controller;

import br.com.seuorg.artistas_api.application.dto.AuthResponse;
import br.com.seuorg.artistas_api.application.dto.LoginRequestDTO;
import br.com.seuorg.artistas_api.application.dto.UsuarioCreateDTO;
import br.com.seuorg.artistas_api.application.service.UsuarioService;
import br.com.seuorg.artistas_api.domain.entity.Usuario;
import br.com.seuorg.artistas_api.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public UsuarioController(UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/api/usuarios")
    public ResponseEntity<Usuario> cadastrar(@RequestBody @Valid UsuarioCreateDTO dto) {
        Usuario usuario = usuarioService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuario.getId())
                .toUri();
        return ResponseEntity.created(location).body(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequestDTO dto) {
        Usuario usuario = usuarioService.autenticar(dto);
        String token = jwtUtil.generateToken(usuario);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
