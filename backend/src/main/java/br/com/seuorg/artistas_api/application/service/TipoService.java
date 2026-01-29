package br.com.seuorg.artistas_api.application.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TipoService {

    public TipoService() {
        // Serviço de Tipos foi removido; manter stub para compatibilidade
    }

    public Object criar(Object dto) {
        throw new UnsupportedOperationException("Recurso 'tipos' removido");
    }

    public Object obterPorId(Long id) {
        throw new UnsupportedOperationException("Recurso 'tipos' removido");
    }

    public Object listarTodos(Pageable pageable) {
        throw new UnsupportedOperationException("Recurso 'tipos' removido");
    }

    public java.util.List<Object> listarTodos() {
        throw new UnsupportedOperationException("Recurso 'tipos' removido");
    }

    public Object atualizar(Long id, Object dto) {
        throw new UnsupportedOperationException("Recurso 'tipos' removido");
    }

    public void deletar(Long id) {
        throw new UnsupportedOperationException("Recurso 'tipos' removido");
    }
}
