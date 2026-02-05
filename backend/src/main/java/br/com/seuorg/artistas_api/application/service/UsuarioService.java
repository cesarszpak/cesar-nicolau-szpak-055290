package br.com.seuorg.artistas_api.application.service;

import br.com.seuorg.artistas_api.application.dto.LoginRequestDTO;
import br.com.seuorg.artistas_api.application.dto.UsuarioCreateDTO;
import br.com.seuorg.artistas_api.application.dto.UsuarioResponseDTO;
import br.com.seuorg.artistas_api.domain.entity.Usuario;
import br.com.seuorg.artistas_api.domain.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Serviço responsável pelas regras de negócio relacionadas ao usuário.
 *
 * Esta classe contém as operações de criação, autenticação, consulta,
 * listagem, atualização e exclusão de usuários, além da conversão
 * de entidades para DTOs de resposta.
 */
@Service
public class UsuarioService {

    // Repositório responsável pelo acesso aos dados de usuários
    private final UsuarioRepository repository;

    // Componente responsável por criptografar e validar senhas
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Cria um novo usuário no sistema.
     *
     * @param dto Dados para criação do usuário
     * @return Dados do usuário criado
     */
    public UsuarioResponseDTO criar(UsuarioCreateDTO dto) {
        // Verifica se já existe um usuário com o mesmo e-mail
        Optional<Usuario> existe = repository.findByEmail(dto.getEmail());
        if (existe.isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Cria a entidade usuário
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        // Criptografa a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setCreatedAt(LocalDateTime.now());

        // Salva o usuário no banco de dados
        Usuario saved = repository.save(usuario);

        // Converte a entidade para DTO de resposta
        return convertToResponseDTO(saved);
    }

    /**
     * Autentica um usuário a partir do e-mail e senha.
     *
     * @param dto Dados de login
     * @return Usuário autenticado
     */
    public Usuario autenticar(LoginRequestDTO dto) {
        // Busca o usuário pelo e-mail
        Usuario usuario = repository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));

        // Valida a senha informada com a senha criptografada
        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        return usuario;
    }

    /**
     * Busca um usuário pelo ID.
     *
     * @param id Identificador do usuário
     * @return Dados do usuário encontrado
     */
    public UsuarioResponseDTO obterPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return convertToResponseDTO(usuario);
    }

    /**
     * Busca um usuário pelo email.
     *
     * @param email Email do usuário
     * @return Dados do usuário encontrado
     */
    public UsuarioResponseDTO obterPorEmail(String email) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return convertToResponseDTO(usuario);
    }

    /**
     * Lista todos os usuários de forma paginada.
     *
     * @param pageable Informações de paginação
     * @return Página de usuários
     */
    public Page<UsuarioResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Atualiza os dados de um usuário existente.
     *
     * @param id  Identificador do usuário
     * @param dto Novos dados do usuário
     * @return Dados atualizados do usuário
     */
    public UsuarioResponseDTO atualizar(Long id, UsuarioCreateDTO dto) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Caso o e-mail tenha sido alterado, verifica se já existe outro usuário com ele
        if (!usuario.getEmail().equals(dto.getEmail())) {
            Optional<Usuario> existe = repository.findByEmail(dto.getEmail());
            if (existe.isPresent()) {
                throw new IllegalArgumentException("Email já cadastrado");
            }
        }

        // Atualiza os dados do usuário
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));

        Usuario updated = repository.save(usuario);
        return convertToResponseDTO(updated);
    }

    /**
     * Remove um usuário pelo ID.
     *
     * @param id Identificador do usuário
     */
    public void deletar(Long id) {
        // Verifica se o usuário existe antes de excluir
        if (!repository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }

        repository.deleteById(id);
    }

    /**
     * Converte a entidade Usuario para o DTO de resposta.
     *
     * @param usuario Entidade usuário
     * @return DTO de resposta do usuário
     */
    private UsuarioResponseDTO convertToResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCreatedAt()
        );
    }
}
