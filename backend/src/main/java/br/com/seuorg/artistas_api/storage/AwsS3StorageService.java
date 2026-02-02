package br.com.seuorg.artistas_api.storage;

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
@Service
public class AwsS3StorageService implements S3StorageService {

    // Cliente principal do S3 para operações padrão (upload, download, delete)
    private final S3Client s3;

    // Presigner usado para gerar URLs temporárias (pré-assinadas)
    private final S3Presigner presigner;

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
            s3.putObject(req, RequestBody.fromBytes(bytes));
        } else {
            // Upload normal utilizando stream
            s3.putObject(req, RequestBody.fromInputStream(data, size));
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
