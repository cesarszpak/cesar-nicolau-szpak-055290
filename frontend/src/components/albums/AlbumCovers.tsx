// Importa o React
import React from 'react'

// Importa o DTO da capa do álbum
import type { CapaAlbumDTO } from '../../services/capa.service'

// Importa o serviço responsável pelas operações com capas
import { capaService } from '../../services/capa.service'

// Componente responsável por listar e gerenciar as capas de um álbum
// Props:
// - albumId: identificador do álbum
// - reloadTrigger (opcional): usado para forçar o recarregamento das capas
const AlbumCovers: React.FC<{ albumId: number; reloadTrigger?: number }> = ({ albumId, reloadTrigger }) => {

  // Estado com a lista de capas do álbum
  const [covers, setCovers] = React.useState<CapaAlbumDTO[]>([])

  // Estado para controle de carregamento
  const [loading, setLoading] = React.useState(false)

  // Estado para armazenar mensagens de erro
  const [error, setError] = React.useState<string | null>(null)

  // Função responsável por carregar as capas do álbum
  const load = React.useCallback(() => {
    setLoading(true)
    setError(null)

    // Busca as capas associadas ao álbum
    capaService.listByAlbum(albumId)
      .then(r => setCovers(r)) // Atualiza a lista de capas
      .catch(e => setError((e as Error).message)) // Captura erro
      .finally(() => setLoading(false)) // Finaliza o loading
  }, [albumId])

  // Executa o carregamento inicial e quando o trigger mudar
  React.useEffect(() => void load(), [load, reloadTrigger])

  // Recarrega explicitamente quando o trigger for alterado
  // (ex.: após upload bem-sucedido)
  React.useEffect(() => {
    if (typeof reloadTrigger !== 'undefined') load()
  }, [reloadTrigger, load])

  // Função responsável por excluir uma capa
  const handleDelete = async (id: number) => {
    // Usa o serviço de confirmação (SweetAlert2) com mensagens em português
    const { confirmDelete } = await import('../../services/confirm.service')
    const ok = await confirmDelete('a imagem')
    if (!ok) return

    // Remove a capa pelo serviço
    capaService.remove(id)
      .then(() =>
        // Remove a capa excluída do estado local
        setCovers(prev => prev.filter(c => c.id !== id))
      )
      .catch(e => setError((e as Error).message))
  }

  // Exibe estado de carregamento
  if (loading) return <div className="text-gray-500">Carregando capas...</div>

  // Exibe erro, se houver
  if (error) return <div className="text-red-500">{error}</div>

  // Exibe mensagem caso não haja capas
  if (!covers.length) return <div className="text-gray-500">Nenhuma capa encontrada</div>

  return (
    // Grid responsivo para exibição das capas
    <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
      {covers.map(c => (
        <div
          key={c.id}
          className="relative overflow-hidden rounded shadow-sm"
        >
          {/* Imagem da capa */}
          <img
            src={c.url}
            alt={c.nomeArquivo}
            className="album-cover"
            onError={(e) => {
              const img = e.currentTarget as HTMLImageElement

              // Se falhar usando o endpoint público, tenta buscar via proxy da API
              if (img.src && img.src.startsWith('http')) {
                img.src = `/api/capas/${c.id}/conteudo`
              } else {
                // Caso falhe novamente, exibe um placeholder SVG
                img.src =
                  'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200"><rect width="100%" height="100%" fill="%23eee"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%23999" font-family="Arial" font-size="14">Imagem indisponível</text></svg>'
              }
            }}
          />

          {/* Botão para excluir a capa */}
          <button
            title="Excluir"
            onClick={() => handleDelete(c.id)}
            className="absolute top-1 right-1 bg-white/80 hover:bg-white rounded-full p-1 shadow"
          >
            ✖
          </button>
        </div>
      ))}
    </div>
  )
}

// Exporta o componente
export default AlbumCovers
