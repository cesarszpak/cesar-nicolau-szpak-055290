package br.com.seuorg.artistas_api.controller;

import br.com.seuorg.artistas_api.application.dto.CapaAlbumResponseDTO;
import br.com.seuorg.artistas_api.application.service.CapaAlbumService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controller responsável pelo gerenciamento das capas de álbuns.
 *
 * Expõe endpoints REST para criação, listagem, edição e exclusão
 * de capas associadas aos álbuns.
 */
@RestController
@RequestMapping("/api/capas")
public class CapaAlbumController {

    /** Service responsável pelas regras de negócio das capas de álbuns */
    private final CapaAlbumService capaService;

    public CapaAlbumController(CapaAlbumService capaService) {
        this.capaService = capaService;
    }

    /**
     * Cria novas capas para um álbum.
     *
     * Recebe arquivos multipart e associa cada um deles
     * ao álbum informado.
     *
     * @param albumId  identificador do álbum
     * @param arquivos lista de arquivos enviados
     * @return lista de capas criadas
     * @throws IOException em caso de erro no upload dos arquivos
     */
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<List<CapaAlbumResponseDTO>> criar(
            @RequestParam @NotNull Long albumId,
            @RequestParam("arquivos") List<MultipartFile> arquivos
    ) throws IOException {

        List<CapaAlbumResponseDTO> dto = capaService.criar(albumId, arquivos);
        return ResponseEntity.ok(dto);
    }

    /**
     * Lista todas as capas associadas a um álbum.
     *
     * @param albumId identificador do álbum
     * @return lista de capas do álbum
     */
    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<CapaAlbumResponseDTO>> listarPorAlbum(
            @PathVariable Long albumId
    ) {
        return ResponseEntity.ok(capaService.listarPorAlbum(albumId));
    }

    /**
     * Exclui uma capa de álbum.
     *
     * Remove o arquivo do armazenamento e o registro
     * correspondente no banco de dados.
     *
     * @param id identificador da capa do álbum
     * @return resposta sem conteúdo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        capaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Atualiza as capas de um álbum.
     *
     * Permite adicionar novas capas e/ou remover capas existentes
     * em uma única requisição.
     *
     * @param albumId        identificador do álbum
     * @param arquivos       arquivos a serem adicionados (opcional)
     * @param idsParaRemover identificadores das capas a serem removidas (opcional)
     * @return lista de capas adicionadas
     * @throws IOException em caso de erro no upload dos arquivos
     */
    @PutMapping(value = "/album/{albumId}", consumes = {"multipart/form-data"})
    public ResponseEntity<List<CapaAlbumResponseDTO>> editar(
            @PathVariable Long albumId,
            @RequestParam(value = "arquivos", required = false) List<MultipartFile> arquivos,
            @RequestParam(value = "idsParaRemover", required = false) List<Long> idsParaRemover
    ) throws IOException {

        List<CapaAlbumResponseDTO> result =
                capaService.atualizar(albumId, arquivos, idsParaRemover);

        return ResponseEntity.ok(result);
    }
}
