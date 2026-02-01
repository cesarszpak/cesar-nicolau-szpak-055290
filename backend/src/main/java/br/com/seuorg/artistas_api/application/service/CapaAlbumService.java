package br.com.seuorg.artistas_api.application.service;

import br.com.seuorg.artistas_api.application.dto.CapaAlbumResponseDTO;
import br.com.seuorg.artistas_api.domain.entity.Album;
import br.com.seuorg.artistas_api.domain.entity.CapaAlbum;
import br.com.seuorg.artistas_api.domain.repository.AlbumRepository;
import br.com.seuorg.artistas_api.domain.repository.CapaAlbumRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service responsável pelo gerenciamento das capas de álbuns.
 *
 * Esta classe contém as regras de negócio para criação, listagem,
 * atualização e exclusão de capas de álbuns, incluindo o upload
 * e remoção dos arquivos no S3.
 */
@Service
public class CapaAlbumService {

    /** Repositório responsável pelas operações de persistência das capas de álbum */
    private final CapaAlbumRepository capaRepo;

    /** Repositório responsável pelas operações relacionadas aos álbuns */
    private final AlbumRepository albumRepo;

    /** Serviço responsável pela comunicação com o armazenamento S3 */
    private final S3StorageService s3;

    /** Nome do bucket onde as capas de álbuns são armazenadas */
    @Value("${s3.bucket}")
    private String bucket;

    /** URL base pública para acesso aos arquivos armazenados no S3 */
    @Value("${s3.public-base-url}")
    private String publicBaseUrl;

    public CapaAlbumService(CapaAlbumRepository capaRepo, AlbumRepository albumRepo, S3StorageService s3) {
        this.capaRepo = capaRepo;
        this.albumRepo = albumRepo;
        this.s3 = s3;
    }

    /**
     * Cria e associa capas a um álbum existente.
     *
     * Realiza o upload dos arquivos para o S3 e persiste
     * as informações da capa no banco de dados.
     *
     * @param albumId  identificador do álbum
     * @param arquivos lista de arquivos enviados
     * @return lista de capas criadas
     * @throws IOException em caso de erro no upload dos arquivos
     */
    public List<CapaAlbumResponseDTO> criar(Long albumId, List<MultipartFile> arquivos) throws IOException {
        Album album = albumRepo.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Album não encontrado"));

        List<CapaAlbum> saved = new ArrayList<>();

        for (MultipartFile f : arquivos) {
            // Gera a chave única do arquivo no S3
            String key = String.format("albums/%d/%s-%s",
                    albumId, UUID.randomUUID(), f.getOriginalFilename());

            // Realiza o upload do arquivo para o S3
            s3.upload(bucket, key, f.getInputStream(), f.getSize(), f.getContentType());

            // Cria a entidade de capa do álbum
            CapaAlbum c = new CapaAlbum();
            c.setAlbum(album);
            c.setChave(key);
            c.setNomeArquivo(f.getOriginalFilename());
            c.setContentType(f.getContentType());
            c.setTamanho(f.getSize());

            // Salva a capa no banco de dados
            saved.add(capaRepo.save(c));
        }

        // Converte as entidades para DTO de resposta
        return saved.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Lista todas as capas associadas a um álbum.
     *
     * @param albumId identificador do álbum
     * @return lista de capas do álbum
     */
    public List<CapaAlbumResponseDTO> listarPorAlbum(Long albumId) {
        return capaRepo.findByAlbumId(albumId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Exclui uma capa de álbum.
     *
     * Remove o arquivo do S3 e exclui o registro do banco de dados.
     *
     * @param id identificador da capa do álbum
     */
    public void excluir(Long id) {
        Optional<CapaAlbum> oc = capaRepo.findById(id);
        if (oc.isEmpty()) return;

        CapaAlbum capa = oc.get();

        // Remove o arquivo do S3
        s3.delete(bucket, capa.getChave());

        // Remove o registro do banco de dados
        capaRepo.delete(capa);
    }

    /**
     * Atualiza as capas de um álbum.
     *
     * Permite remover capas existentes e adicionar novas capas
     * em uma única operação.
     *
     * @param albumId               identificador do álbum
     * @param arquivosParaAdicionar arquivos que serão adicionados
     * @param idsParaRemover        identificadores das capas a serem removidas
     * @return lista de capas adicionadas
     * @throws IOException em caso de erro no upload dos arquivos
     */
    public List<CapaAlbumResponseDTO> atualizar(
            Long albumId,
            List<MultipartFile> arquivosParaAdicionar,
            List<Long> idsParaRemover
    ) throws IOException {

        // Remove as capas informadas
        if (idsParaRemover != null) {
            for (Long id : idsParaRemover) {
                excluir(id);
            }
        }

        // Adiciona novas capas, se existirem
        if (arquivosParaAdicionar != null && !arquivosParaAdicionar.isEmpty()) {
            return criar(albumId, arquivosParaAdicionar);
        }

        return new ArrayList<>();
    }

    /**
     * Converte a entidade CapaAlbum para o DTO de resposta.
     *
     * @param c entidade de capa do álbum
     * @return DTO preenchido com os dados da capa
     */
    private CapaAlbumResponseDTO toDto(CapaAlbum c) {
        CapaAlbumResponseDTO dto = new CapaAlbumResponseDTO();
        dto.setId(c.getId());
        dto.setAlbumId(c.getAlbum().getId());
        dto.setChave(c.getChave());
        dto.setNomeArquivo(c.getNomeArquivo());
        dto.setContentType(c.getContentType());
        dto.setTamanho(c.getTamanho());
        dto.setCreatedAt(c.getCreatedAt());

        // Monta a URL pública de acesso à capa
        dto.setUrl(String.format(
                "%s/%s/%s",
                publicBaseUrl.replaceAll("/+$", ""),
                bucket,
                c.getChave()
        ));

        return dto;
    }
}
