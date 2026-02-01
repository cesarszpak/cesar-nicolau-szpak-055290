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

    /**
     * Inicializa o cliente S3 após a injeção das propriedades.
     *
     * Configura as credenciais, região e, caso informado,
     * o endpoint customizado.
     */
    @PostConstruct
    private void init() {
        var b = S3Client.builder()
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .region(Region.of(region));

        // Define um endpoint customizado caso esteja configurado
        if (endpoint != null && !endpoint.isEmpty()) {
            b = b.endpointOverride(URI.create(endpoint));
        }

        this.s3 = b.build();
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
}
