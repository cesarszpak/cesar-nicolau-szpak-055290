package br.com.seuorg.artistas_api.storage;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

/**
 * Interface que define o contrato para serviços de armazenamento S3.
 *
 * Esta interface abstrai as operações básicas de armazenamento de arquivos,
 * permitindo que diferentes implementações (AWS S3, MinIO, etc.)
 * sejam utilizadas sem impactar as regras de negócio da aplicação.
 */
public interface S3StorageService {

    /**
     * Realiza o upload de um arquivo para o armazenamento.
     *
     * @param bucket      nome do bucket onde o arquivo será armazenado
     * @param key         chave/caminho do arquivo no bucket
     * @param data        stream de dados do arquivo
     * @param size        tamanho do arquivo em bytes
     * @param contentType tipo MIME do arquivo
     * @throws IOException em caso de falha durante o upload
     */
    void upload(String bucket, String key, InputStream data, long size, String contentType) throws IOException;

    /**
     * Remove um arquivo do armazenamento.
     *
     * @param bucket nome do bucket
     * @param key    chave/caminho do arquivo no bucket
     */
    void delete(String bucket, String key);

    /**
     * Gera uma URL pré-assinada para acesso temporário ao arquivo.
     *
     * @param bucket     nome do bucket
     * @param key        chave/caminho do arquivo no bucket
     * @param expiration tempo de validade da URL
     * @return URL pré-assinada para acesso ao arquivo
     */
    String generatePresignedUrl(String bucket, String key, Duration expiration);

    /**
     * Realiza o download do conteúdo de um arquivo.
     *
     * As implementações devem garantir uma leitura eficiente do stream.
     *
     * @param bucket nome do bucket
     * @param key    chave/caminho do arquivo no bucket
     * @return conteúdo do arquivo em bytes
     * @throws IOException em caso de falha durante o download
     */
    byte[] download(String bucket, String key) throws IOException;
}
