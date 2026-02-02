package br.com.seuorg.artistas_api.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URI;

/**
 * Service responsável pela comunicação com o armazenamento S3.
 *
 * Esta classe centraliza as operações de upload e exclusão de arquivos
 * em um bucket S3 ou serviço compatível (ex: MinIO).
 */
@Service
public class S3StorageService {

    /** Endpoint do serviço S3 (opcional, usado para serviços compatíveis como MinIO) */
    @Value("${s3.endpoint}")
    private String endpoint;

    /** Chave de acesso ao serviço S3 */
    @Value("${s3.access-key}")
    private String accessKey;

    /** Chave secreta de acesso ao serviço S3 */
    @Value("${s3.secret-key}")
    private String secretKey;

    /** Região configurada para o serviço S3 */
    @Value("${s3.region}")
    private String region;

    /** Cliente S3 utilizado para realizar as operações */
    private S3Client s3;
    private io.minio.MinioClient minioClient;

    /** URL pública base configurada para o storage (usada como fallback ao gerar URLs) */
    @Value("${s3.public-base-url:}")
    private String publicBaseUrl;

    /**
     * Inicializa o cliente S3 e o presigner após a injeção das propriedades.
     *
     * Configura as credenciais, região e, caso informado,
     * o endpoint customizado.
     */
    @PostConstruct
    private void init() {
        var creds = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        var b = S3Client.builder()
                .credentialsProvider(creds)
                .region(Region.of(region));

        // Se endpoint customizado (ex: MinIO) está configurado, força path-style
        // para evitar que o SDK use virtual-host style (p.ex. bucket.minio), o que
        // causa resolução de nomes como 'capas.minio' que não existem na rede.
        if (endpoint != null && !endpoint.isEmpty()) {
            b = b.endpointOverride(URI.create(endpoint))
                 .serviceConfiguration(software.amazon.awssdk.services.s3.S3Configuration.builder()
                         .pathStyleAccessEnabled(true)
                         .build());
        }

        this.s3 = b.build();

        // Try to initialize MinIO client (optional) for presigned url generation
        try {
            io.minio.MinioClient m = io.minio.MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
            this.minioClient = m;
        } catch (NoClassDefFoundError | Exception e) {
            // MinIO client not available, presigned generation will fallback
            this.minioClient = null;
        }
    }

    /**
     * Realiza o upload de um arquivo para o bucket S3.
     *
     * @param bucket      nome do bucket
     * @param key         chave/identificador do arquivo no bucket
     * @param inputStream stream do arquivo a ser enviado
     * @param size        tamanho do arquivo em bytes
     * @param contentType tipo de conteúdo (MIME type)
     */
    public void upload(
            String bucket,
            String key,
            InputStream inputStream,
            long size,
            String contentType
    ) {
        // Garantir que o bucket exista - se não existe, tenta criar (útil para ambientes de desenvolvimento com MinIO)
        try {
            s3.headBucket(software.amazon.awssdk.services.s3.model.HeadBucketRequest.builder().bucket(bucket).build());
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            // Se o erro indica que o bucket não existe, tenta criar
            if (e.statusCode() == 404) {
                try {
                    s3.createBucket(software.amazon.awssdk.services.s3.model.CreateBucketRequest.builder().bucket(bucket).build());
                } catch (Exception ex) {
                    // fallback: rethrow original exception
                    throw e;
                }
            } else {
                throw e;
            }
        }

        PutObjectRequest por = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3.putObject(por, RequestBody.fromInputStream(inputStream, size));
    }

    /**
     * Remove um arquivo do bucket S3.
     *
     * @param bucket nome do bucket
     * @param key    chave/identificador do arquivo no bucket
     */
    public void delete(String bucket, String key) {
        DeleteObjectRequest dor = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3.deleteObject(dor);
    }

    /**
     * Gera uma URL pré-assinada para acesso ao objeto no bucket.
     *
     * @param bucket     nome do bucket
     * @param key        chave do objeto
     * @param expiration duração da assinatura
     * @return URL pré-assinada
     */
    public String generatePresignedUrl(String bucket, String key, java.time.Duration expiration) {
        // Prefer MinIO client if available (easier to generate presigned URLs for MinIO)
        if (minioClient != null) {
            try {
                int expirySeconds = Math.toIntExact(expiration.getSeconds());
                // MinIO's API expects expiry in seconds and returns a URL
                return minioClient.getPresignedObjectUrl(
                        io.minio.GetPresignedObjectUrlArgs.builder()
                                .method(io.minio.http.Method.GET)
                                .bucket(bucket)
                                .object(key)
                                .expiry(expirySeconds)
                                .build()
                );
            } catch (Exception e) {
                // fallback
            }
        }

        // Fallback: return public URL
        return String.format("%s/%s/%s", publicBaseUrl == null ? "" : publicBaseUrl.replaceAll("/+$", ""), bucket, key);
    }
}
