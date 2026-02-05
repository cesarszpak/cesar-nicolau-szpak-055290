// Importa o React e seus hooks
import React from 'react'

// Importa hooks do React Router para acessar parâmetros da rota e navegação
import { useParams, useNavigate } from 'react-router-dom'

// Importa o serviço de álbum e o tipo Album
import { albumService, type Album } from '../services/album.service'

// Importa o componente responsável por exibir as capas do álbum
import AlbumCovers from '../components/albums/AlbumCovers'

// Importa o formulário de upload de novas capas
import AlbumUploadForm from '../components/albums/AlbumUploadForm'

// Componente responsável por exibir os detalhes de um álbum
const AlbumView: React.FC = () => {

  // Recupera o parâmetro "id" da URL
  const { id } = useParams<{ id: string }>()

  // Converte o id para número
  const albumId = Number(id)

  // Hook para navegação entre páginas
  const navigate = useNavigate()

  // Estado para armazenar os dados do álbum
  const [album, setAlbum] = React.useState<Album | null>(null)

  // Estado para controle de carregamento
  const [loading, setLoading] = React.useState(false)

  // Estado para armazenar mensagens de erro
  const [error, setError] = React.useState<string | null>(null)

  // Estado usado como gatilho para recarregar as capas do álbum
  const [coversReloadTrigger, setCoversReloadTrigger] = React.useState(0)

  // Efeito executado ao carregar o componente ou quando o albumId muda
  React.useEffect(() => {
    // Se não houver ID válido, não executa a busca
    if (!albumId) return

    // Ativa o loading
    setLoading(true)

    // Busca os dados do álbum pelo serviço
    albumService.get(albumId)
      .then(a => setAlbum(a)) // Atualiza o estado com os dados do álbum
      .catch(e => setError((e as Error).message)) // Captura e exibe erro
      .finally(() => setLoading(false)) // Finaliza o loading
  }, [albumId])

  return (
    <div className="site-container p-6">

      {/* Cabeçalho da página */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold">Detalhes do Álbum</h1>
          {/* <div className="text-sm text-gray-600">Informações e capas</div> */}
        </div>

        {/* Botão para voltar à página anterior */}
        <div>
          <button
            onClick={() => navigate(-1)}
            className="btn-secondary-md"
          >
            Voltar
          </button>
        </div>
      </div>

      {/* Exibe tela de carregamento */}
      {loading && (
        <div className="loading-screen">
          <div className="spinner" />
        </div>
      )}

      {/* Exibe mensagem de erro, se houver */}
      {error && <div className="alert-danger">{error}</div>}

      {/* Exibe os dados do álbum quando carregado */}
      {album && (
        <div className="mb-6">

          {/* Nome do álbum */}
          <div className="text-xl font-semibold">
            {album.nome}
          </div>

          {/* Nome do artista */}
          <div className="text-sm text-gray-600">
            Artista: {album.artistaNome}
          </div>

          {/* Listagem das capas do álbum */}
          <div className="mt-4">
            <AlbumCovers
              albumId={album.id}
              reloadTrigger={coversReloadTrigger}
              isDetailPage={true}
            />
          </div>

          {/* Formulário para upload de novas capas */}
          <div className="mt-4">
            <AlbumUploadForm
              albumId={album.id}
              onUploaded={() => {
                // Incrementa o trigger para forçar o reload das capas
                setCoversReloadTrigger(t => t + 1)
              }}
            />
          </div>
        </div>
      )}

    </div>
  )
}

// Exporta o componente
export default AlbumView
