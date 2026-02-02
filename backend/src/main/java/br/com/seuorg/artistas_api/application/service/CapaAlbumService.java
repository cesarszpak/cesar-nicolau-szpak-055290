package br.com.seuorg.artistas_api.application.service;

import br.com.seuorg.artistas_api.application.dto.CapaAlbumResponseDTO;
import br.com.seuorg.artistas_api.domain.entity.Album;
import br.com.seuorg.artistas_api.domain.entity.CapaAlbum;
import br.com.seuorg.artistas_api.domain.repository.AlbumRepository;
import br.com.seuorg.artistas_api.domain.repository.CapaAlbumRepository;
import br.com.seuorg.artistas_api.storage.S3StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelo gerenciamento das capas de álbuns.
 *
 * Esta classe contém as regras de negócio para criação, listagem,
 * atualização e exclusão de capas de álbuns, incluindo o upload
 * e a remoção dos arquivos no S3.
 */
@Service
public class CapaAlbumService {

    /** Logger da aplicação */
    private static final Logger log = LoggerFactory.getLogger(CapaAlbumService.class);

    /** Repositório responsável pela persistência das capas de álbum */
    private final CapaAlbumRepository capaRepo;

    /** Repositório responsável pelas operações relacionadas aos álbuns */
    private final AlbumRepository albumRepo;

    /** Serviço responsável pela comunicação com o armazenamento S3 */
    private final S3StorageService s3;

    /** Nome do bucket onde as capas de álbuns são armazenadas */
    @Value("${s3.bucket}")
    private String bucket;

    /**
     * URL base pública para acesso aos arquivos armazenados no S3.
     * Utilizada como fallback quando a URL pré-assinada não estiver disponível.
     */
    @Value("${s3.public-base-url}")
    private String publicBaseUrl;

    /** Quantidade máxima de arquivos permitidos por upload */
    @Value("${s3.max-files-per-upload:10}")
    private int maxFilesPerUpload;

    /**
     * Construtor com injeção de dependências.
     *
     * @param capaRepo repositório de capas de álbum
     * @param albumRepo repositório de álbuns
     * @param s3 serviço de armazenamento S3
     */
    public CapaAlbumService(
            CapaAlbumRepository capaRepo,
            AlbumRepository albumRepo,
            S3StorageService s3
    ) {
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
        // Busca o álbum pelo ID
        Album album = albumRepo.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Album não encontrado"));

        // Valida quantidade máxima de arquivos
        if (arquivos.size() > maxFilesPerUpload) {
            throw new IllegalArgumentException(
                    "Limite de arquivos por upload excedido: máximo " + maxFilesPerUpload
            );
        }

        List<CapaAlbum> saved = new ArrayList<>();

        for (MultipartFile f : arquivos) {

            // Obtém o nome original do arquivo
            String original = f.getOriginalFilename() == null ? "file" : f.getOriginalFilename();

            // Separa nome base e extensão
            String baseName = original;
            String ext = "";
            int dot = original.lastIndexOf('.');
            if (dot > 0) {
                baseName = original.substring(0, dot);
                ext = original.substring(dot + 1);
            }

            // Gera um slug amigável a partir do nome do arquivo
            String slug = slugify(baseName);

            // Gera a chave única do arquivo no S3
            String key = String.format(
                    "capas-album/albums/%d/%s-%s%s",
                    albumId,
                    slug,
                    UUID.randomUUID(),
                    ext.isEmpty() ? "" : ("." + ext)
            );

            // Realiza o upload do arquivo para o S3
            log.info(
                    "Upload de capa - albumId={}, arquivo={}, tamanho={}, contentType={}, key={}",
                    albumId, original, f.getSize(), f.getContentType(), key
            );
            s3.upload(bucket, key, f.getInputStream(), f.getSize(), f.getContentType());

            // Cria a entidade de capa do álbum
            CapaAlbum capa = new CapaAlbum();
            capa.setAlbum(album);
            capa.setChave(key);
            capa.setNomeArquivo(original);
            capa.setContentType(f.getContentType());
            capa.setTamanho(f.getSize());

            // Persiste a capa no banco de dados
            saved.add(capaRepo.save(capa));
        }

        // Converte as entidades salvas para DTO de resposta
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
        return capaRepo.findByAlbumId(albumId)
                .stream()
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
     * @param albumId identificador do álbum
     * @param arquivosParaAdicionar arquivos que serão adicionados
     * @param idsParaRemover identificadores das capas a serem removidas
     * @return lista de capas adicionadas
     * @throws IOException em caso de erro no upload
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
     * Gera um slug amigável a partir de um texto.
     *
     * @param input texto original
     * @return slug normalizado
     */
    private static String slugify(String input) {
        if (input == null) return "";

        String s = java.text.Normalizer
                .normalize(input, java.text.Normalizer.Form.NFKD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(java.util.Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        return s.isEmpty() ? "file" : s;
    }

    /**
     * Converte a entidade CapaAlbum para o DTO de resposta.
     *
     * Também gera a URL de acesso ao arquivo (pré-assinada ou pública).
     *
     * @param c entidade de capa do álbum
     * @return DTO preenchido
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

        // Gera URL pré-assinada com 30 minutos de expiração
        try {
            String presigned = s3.generatePresignedUrl(
                    bucket,
                    c.getChave(),
                    java.time.Duration.ofMinutes(30)
            );

            // Substitui a base da URL caso exista uma URL pública configurada
            if (publicBaseUrl != null && !publicBaseUrl.trim().isEmpty()) {
                try {
                    java.net.URI p = java.net.URI.create(presigned);
                    String base = publicBaseUrl.replaceAll("/+$", "");
                    String replaced = base + p.getRawPath()
                            + (p.getRawQuery() == null ? "" : "?" + p.getRawQuery());
                    dto.setUrl(replaced);
                } catch (Exception e) {
                    dto.setUrl(presigned);
                }
            } else {
                // Fallback para endpoint da API (proxy)
                dto.setUrl(String.format("/api/capas/%d/conteudo", c.getId()));
            }

        } catch (Exception ex) {
            // Fallback final para URL pública direta
            if (publicBaseUrl != null && !publicBaseUrl.trim().isEmpty()) {
                dto.setUrl(String.format(
                        "%s/%s/%s",
                        publicBaseUrl.replaceAll("/+$", ""),
                        bucket,
                        c.getChave()
                ));
            } else {
                dto.setUrl(String.format("/api/capas/%d/conteudo", c.getId()));
            }
        }

        return dto;
    }

    /**
     * Busca uma capa pelo ID.
     *
     * @param id identificador da capa
     * @return DTO da capa ou null se não encontrada
     */
    public CapaAlbumResponseDTO findById(Long id) {
        Optional<CapaAlbum> oc = capaRepo.findById(id);
        if (oc.isEmpty()) return null;
        return toDto(oc.get());
    }

    /**
     * Realiza o download do conteúdo da capa diretamente do S3.
     *
     * @param key chave do arquivo no S3
     * @return conteúdo do arquivo em bytes
     * @throws IOException em caso de erro no download
     */
    public byte[] downloadContent(String key) throws IOException {
        return s3.download(bucket, key);
    }
}
