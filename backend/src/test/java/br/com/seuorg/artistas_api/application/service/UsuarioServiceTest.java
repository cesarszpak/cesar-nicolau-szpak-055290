package br.com.seuorg.artistas_api.application.service;

import br.com.seuorg.artistas_api.application.dto.UsuarioCreateDTO;
import br.com.seuorg.artistas_api.domain.entity.Usuario;
import br.com.seuorg.artistas_api.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de testes unitários para o serviço de usuários (UsuarioService).
 *
 * Valida as regras de negócio relacionadas à criação e
 * autenticação de usuários.
 */
class UsuarioServiceTest {

    /** Mock do repositório de usuários */
    @Mock
    private UsuarioRepository usuarioRepository;

    /** Mock do codificador de senhas */
    @Mock
    private PasswordEncoder passwordEncoder;

    /** Serviço a ser testado, com dependências mockadas */
    @InjectMocks
    private UsuarioService service;

    /**
     * Inicializa os mocks antes da execução de cada teste.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testa a criação de usuário quando o e-mail já existe.
     *
     * Deve lançar uma exceção ao tentar criar um usuário
     * com um e-mail já cadastrado.
     */
    @Test
    void criar_emailExistente_deveLancar() {
        UsuarioCreateDTO dto = new UsuarioCreateDTO();
        dto.setNome("x");
        dto.setEmail("a@a.com");
        dto.setSenha("123");

        // Simula a existência de um usuário com o e-mail informado
        when(usuarioRepository.findByEmail("a@a.com"))
                .thenReturn(Optional.of(new Usuario()));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.criar(dto)
        );
    }

    /**
     * Testa a autenticação com senha inválida.
     *
     * Deve lançar uma exceção quando a senha informada
     * não corresponde à senha armazenada.
     */
    @Test
    void autenticar_senhaInvalida_deveLancar() {
        when(usuarioRepository.findByEmail("a@a.com"))
                .thenReturn(Optional.of(new Usuario() {{
                    setSenha("encoded");
                }}));

        // Simula falha na validação da senha
        when(passwordEncoder.matches(any(), any()))
                .thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.autenticar(null)
        );
    }
}
