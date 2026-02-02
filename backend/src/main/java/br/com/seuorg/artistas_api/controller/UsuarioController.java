package br.com.seuorg.artistas_api.controller;

import br.com.seuorg.artistas_api.application.dto.AuthResponse;
import br.com.seuorg.artistas_api.application.dto.LoginRequestDTO;
import br.com.seuorg.artistas_api.application.dto.UsuarioCreateDTO;
import br.com.seuorg.artistas_api.application.dto.UsuarioResponseDTO;
import br.com.seuorg.artistas_api.application.service.UsuarioService;
import br.com.seuorg.artistas_api.domain.entity.Usuario;
import br.com.seuorg.artistas_api.security.JwtUtil;
import br.com.seuorg.artistas_api.application.service.RefreshTokenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;

/**
 * Controller responsável por gerenciar usuários e autenticação.
 * Inclui endpoints de CRUD para usuários, login e refresh de tokens JWT.
 */
@RestController
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public UsuarioController(UsuarioService usuarioService, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * Endpoint para cadastro de novos usuários.
     *
     * @param dto Dados do usuário a serem cadastrados
     * @return Detalhes do usuário criado com URI de localização
     */
    @PostMapping("/api/usuarios")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody @Valid UsuarioCreateDTO dto) {
        UsuarioResponseDTO usuario = usuarioService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuario.getId())
                .toUri();
        return ResponseEntity.created(location).body(usuario);
    }

    /**
     * Obtém um usuário pelo ID.
     *
     * @param id ID do usuário
     * @return Usuário correspondente
     */
    @GetMapping("/api/usuarios/{id}")
    public ResponseEntity<UsuarioResponseDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obterPorId(id));
    }

    /**
     * Lista todos os usuários com paginação.
     *
     * @param page Página inicial
     * @param size Tamanho da página
     * @return Página de usuários
     */
    @GetMapping("/api/usuarios")
    public ResponseEntity<Page<UsuarioResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(usuarioService.listarTodos(pageable));
    }

    /**
     * Atualiza os dados de um usuário existente.
     *
     * @param id  ID do usuário
     * @param dto Novos dados do usuário
     * @return Usuário atualizado
     */
    @PutMapping("/api/usuarios/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioCreateDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    /**
     * Deleta um usuário pelo ID.
     *
     * @param id ID do usuário
     * @return Resposta sem conteúdo (204)
     */
    @DeleteMapping("/api/usuarios/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Realiza login do usuário, gerando JWT e refresh token.
     *
     * @param dto Dados de login (email e senha)
     * @return JWT e refresh token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequestDTO dto) {
        Usuario usuario = usuarioService.autenticar(dto);
        String token = jwtUtil.generateToken(usuario);

        // Cria refresh token
        var refresh = refreshTokenService.createRefreshToken(usuario);
        return ResponseEntity.ok(new AuthResponse(token, refresh.getToken()));
    }

    /**
     * Atualiza o JWT usando um refresh token.
     *
     * @param refreshToken Token de refresh antigo
     * @return Novo JWT e refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody String refreshToken) {
        // Verifica se o refresh token existe
        var maybe = refreshTokenService.findByToken(refreshToken);
        if (maybe.isEmpty()) return ResponseEntity.<AuthResponse>status(401).build();

        var rt = maybe.get();
        // Verifica se expirou
        if (rt.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            refreshTokenService.deleteByToken(refreshToken);
            return ResponseEntity.<AuthResponse>status(401).build();
        }

        Usuario usuario = rt.getUsuario();
        String token = jwtUtil.generateToken(usuario);

        // Rotaciona refresh token: deleta o antigo e cria novo
        refreshTokenService.deleteByToken(refreshToken);
        var newRefresh = refreshTokenService.createRefreshToken(usuario);

        return ResponseEntity.ok(new AuthResponse(token, newRefresh.getToken()));
    }
}
