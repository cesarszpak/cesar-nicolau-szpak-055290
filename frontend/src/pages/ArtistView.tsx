// Importa o React
import React from 'react'

// Importa hooks do React Router para leitura de parâmetros da rota e navegação
import { useParams, useNavigate } from 'react-router-dom'

// Importa o serviço de artistas e o tipo Artist
import { artistService, type Artist } from '../services/artist.service'

// Importa o serviço de álbuns e o tipo Album
import { albumService, type Album } from '../services/album.service'

// Quantidade de álbuns exibidos inicialmente
const PAGE_SIZE = 8

// Carregamento lazy do componente AlbumCard para melhorar performance
const AlbumCardLazy = React.lazy(
  () => import('../components/albums/AlbumCard')
) as unknown as React.FC<{
  album: import('../services/album.service').Album
  showUpload?: boolean
}>

// Componente responsável por exibir os dados de um artista e seus álbuns
const ArtistView: React.FC = () => {

  // Recupera o ID do artista a partir da rota
  const { id } = useParams<{ id: string }>()
  const artistId = Number(id)

  // Hook para navegação entre páginas
  const navigate = useNavigate()

  // Estado para armazenar os dados do artista
  const [artist, setArtist] = React.useState<Artist | null>(null)

  // Estado para armazenar os álbuns do artista
  const [albums, setAlbums] = React.useState<Album[]>([])

  // Estado para controle de carregamento
  const [loading, setLoading] = React.useState(false)

  // Estado para armazenar mensagens de erro
  const [error, setError] = React.useState<string | null>(null)

  // Efeito executado ao carregar o componente ou quando o artistId mudar
  React.useEffect(() => {
    if (!artistId) return

    setLoading(true)
    setError(null)

    // Carrega os dados do artista e a lista de álbuns em paralelo
    Promise.all([
      artistService.get(artistId),
      albumService.listByArtist(artistId, 0, PAGE_SIZE)
    ])
      .then(([a, albPage]) => {
        // Atualiza estado com os dados do artista
        setArtist(a as Artist)

        // Atualiza estado com os álbuns do artista
        setAlbums(albPage.content)
      })
      .catch(e => setError((e as Error).message))
      .finally(() => setLoading(false))

  }, [artistId])

  return (
    <div className="site-container p-6">

      {/* Cabeçalho da página */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold">Visualizar Artista</h1>
          {/* <div className="text-sm text-gray-600">
            Dados do artista e lista de álbuns
          </div> */}
        </div>

        {/* Botão para cadastrar um novo álbum */}
        <div className="flex gap-1">
          <button
            onClick={() => navigate(`/artistas/${artistId}/albuns/novo`)}
            className="btn-primary"
          >
            Cadastrar Álbum
          </button>

          {/* Botão para voltar à lista de artistas */}
          <button
            onClick={() => navigate('/artistas')}
            className="btn-secondary"
          >
            Voltar
          </button>
        </div>
      </div>

      {/* Exibe loading */}
      {loading && (
        <div className="loading-screen">
          <div className="spinner" />
        </div>
      )}

      {/* Exibe erro */}
      {error && <div className="alert-danger">{error}</div>}

      {/* Exibe dados do artista */}
      {artist && (
        <div className="mb-6">
          <div className="text-xl font-semibold">{artist.nome}</div>
          {/* <div className="text-sm text-gray-600">#{artist.id}</div> */}
        </div>
      )}

      {/* Seção de álbuns */}
      <div>
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold mb-4">Álbuns</h2>


        </div>

        {/* Mensagem quando não há álbuns */}
        {!albums.length && (
          <div className="text-gray-500">
            Nenhum álbum encontrado
          </div>
        )}

        {/* Grid de álbuns */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {albums.map(alb => (
            <div key={alb.id}>

              {/* Suspense para carregamento lazy do card */}
              <React.Suspense
                fallback={<div className="text-gray-500">Carregando...</div>}
              >
                <AlbumCardLazy
                  album={alb}
                  showUpload={false}
                />
              </React.Suspense>

            </div>
          ))}
        </div>
      </div>

    </div>
  )
}

// Exporta o componente
export default ArtistView
