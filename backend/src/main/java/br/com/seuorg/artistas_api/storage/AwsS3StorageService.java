package br.com.seuorg.artistas_api.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;

/**
 * Implementação do serviço de armazenamento utilizando AWS S3.
 *
 * Esta classe é responsável por realizar operações de upload,
 * download, exclusão de arquivos e geração de URLs pré-assinadas
 * no serviço S3 (ou compatível, como MinIO).
 */
@Slf4j
@Service
public class AwsS3StorageService implements S3StorageService {

    // Cliente principal do S3 para operações padrão (upload, download, delete)
    private final S3Client s3;

    // Presigner usado para gerar URLs temporárias (pré-assinadas)
    private final S3Presigner presigner;
    
    // MinIO client para operações de bucket (opcional)
    private final io.minio.MinioClient minioClient;
    
    // Endpoint do S3 (armazenado para uso posterior)
    private final String endpoint;
    private final String accessKey;
    private final String secretKey;

    /**
     * Construtor responsável por configurar o cliente S3 e o presigner.
     *
     * As propriedades são carregadas a partir do application.yml/properties.
     */
    public AwsS3StorageService(
            @Value("${s3.endpoint}") String endpoint,
            @Value("${s3.access-key}") String accessKey,
            @Value("${s3.secret-key}") String secretKey,
            @Value("${s3.region:us-east-1}") String region
    ) {
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        
        log.info("Inicializando AwsS3StorageService com endpoint: {}", endpoint);
        
        // Cria o provedor de credenciais estáticas
        StaticCredentialsProvider creds = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );

        // Define a região do S3
        Region r = Region.of(region);

        // Configura o cliente S3
        this.s3 = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(r)
                .credentialsProvider(creds)
                // Necessário para serviços compatíveis com S3 (ex: MinIO)
                .forcePathStyle(true)
                .build();

        // Configura o presigner para geração de URLs temporárias
        this.presigner = S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .region(r)
                .credentialsProvider(creds)
                .build();
        
        // Try to initialize MinIO client (optional) for bucket operations
        try {
            this.minioClient = io.minio.MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
            log.info("MinioClient inicializado com sucesso");
        } catch (Exception e) {
            log.warn("MinioClient não disponível ou falhou na inicialização: {}", e.getMessage());
            throw new RuntimeException("Falha ao inicializar MinIO client", e);
        }
    }

    /**
     * Realiza o upload de um arquivo para o S3.
     *
     * @param bucket       nome do bucket
     * @param key          chave/caminho do arquivo no S3
     * @param data         stream de dados do arquivo
     * @param size         tamanho do arquivo
     * @param contentType  tipo MIME do arquivo
     */
    @Override
    public void upload(String bucket, String key, InputStream data, long size, String contentType) throws IOException {
        log.info("Iniciando upload para bucket: {}, key: {}, size: {}", bucket, key, size);
        
        // Garantir que o bucket exista - se não existe, tenta criar
        ensureBucketExists(bucket);
        
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                // contentLength pode ser desconhecido em alguns uploads multipart
                .contentLength(size)
                .build();

        if (size <= 0) {
            // Caso o tamanho não seja conhecido, lê todos os bytes para garantir o envio
            byte[] bytes = data.readAllBytes();
            log.info("Upload de {} bytes (tamanho desconhecido)", bytes.length);
            s3.putObject(req, RequestBody.fromBytes(bytes));
        } else {
            // Upload normal utilizando stream
            log.info("Upload de stream com {} bytes", size);
            s3.putObject(req, RequestBody.fromInputStream(data, size));
        }
        
        log.info("Upload concluído para key: {}", key);
    }
    
    /**
     * Garante que um bucket existe, criando-o se necessário
     */
    private void ensureBucketExists(String bucket) {
        if (minioClient != null) {
            try {
                // Verifica se o bucket existe
                boolean bucketExists = minioClient.bucketExists(
                        io.minio.BucketExistsArgs.builder()
                                .bucket(bucket)
                                .build()
                );
                
                log.info("Bucket '{}' existe? {}", bucket, bucketExists);
                
                // Se não existe, cria o bucket
                if (!bucketExists) {
                    log.info("Criando bucket: {}", bucket);
                    minioClient.makeBucket(
                            io.minio.MakeBucketArgs.builder()
                                    .bucket(bucket)
                                    .build()
                    );
                    log.info("Bucket '{}' criado com sucesso", bucket);
                }
            } catch (Exception e) {
                log.error("Erro ao verificar/criar bucket com MinioClient: {}", e.getMessage(), e);
                throw new RuntimeException("Falha ao verificar/criar bucket: " + bucket, e);
            }
        } else {
            log.warn("MinioClient não disponível, não posso criar bucket automaticamente");
        }
    }

    /**
     * Remove um arquivo do S3.
     *
     * @param bucket nome do bucket
     * @param key    chave do arquivo no S3
     */
    @Override
    public void delete(String bucket, String key) {
        DeleteObjectRequest req = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3.deleteObject(req);
    }

    /**
     * Gera uma URL pré-assinada para download temporário de um arquivo.
     *
     * @param bucket     nome do bucket
     * @param key        chave do arquivo no S3
     * @param expiration tempo de expiração da URL
     * @return URL temporária de acesso ao arquivo
     */
    @Override
    public String generatePresignedUrl(String bucket, String key, Duration expiration) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(getReq)
                .build();

        return presigner.presignGetObject(presignRequest).url().toString();
    }

    /**
     * Realiza o download de um arquivo do S3.
     *
     * @param bucket nome do bucket
     * @param key    chave do arquivo no S3
     * @return conteúdo do arquivo em bytes
     */
    @Override
    public byte[] download(String bucket, String key) throws IOException {
        try {
            var rb = s3.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build(),
                    software.amazon.awssdk.core.sync.ResponseTransformer.toBytes()
            );
            return rb.asByteArray();
        } catch (Exception ex) {
            throw new IOException("Falha ao baixar objeto S3", ex);
        }
    }
}
