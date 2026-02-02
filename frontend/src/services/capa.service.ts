// Importa a instância configurada da API
import api from './api'

// DTO que representa os dados de uma capa de álbum
export interface CapaAlbumDTO {
  id: number              // Identificador da capa
  albumId: number         // ID do álbum ao qual a capa pertence
  chave: string           // Chave interna/única do arquivo
  url: string             // URL pública para acesso à imagem
  nomeArquivo: string     // Nome original do arquivo
  contentType?: string   // Tipo MIME do arquivo (opcional)
  tamanho?: number       // Tamanho do arquivo em bytes (opcional)
}

// Função responsável por realizar o upload de capas para um álbum
// Recebe o ID do álbum e um array de arquivos
const upload = (albumId: number, files: File[]) => {
  const fd = new FormData()

  // Adiciona o ID do álbum ao FormData
  fd.append('albumId', String(albumId))

  // Adiciona todos os arquivos ao FormData
  for (const f of files) {
    fd.append('arquivos', f)
  }

  // Envia os dados para o endpoint de upload
  return api.post('/api/capas', fd)
}

// Lista todas as capas associadas a um álbum
const listByAlbum = (albumId: number) =>
  api.get(`/api/capas/album/${albumId}`) as Promise<CapaAlbumDTO[]>

// Remove uma capa pelo ID
const remove = (id: number) =>
  api.del(`/api/capas/${id}`)

// Exporta o serviço de capas
export const capaService = {
  upload,
  listByAlbum,
  remove
}
