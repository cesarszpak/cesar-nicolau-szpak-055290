// Importa o React
import React from 'react'

// Importa o serviço de álbuns e o tipo Album
import { albumService, type Album } from '../services/album.service'

// Importa o componente de paginação
import Pagination from '../components/Pagination'

// Quantidade de registros por página
const PAGE_SIZE = 10

// Carregamento lazy do componente AlbumCard
// Usado para melhorar performance ao carregar a lista
const AlbumCardLazy = React.lazy(
  () => import('../components/albums/AlbumCard')
) as unknown as React.FC<{
  album: import('../services/album.service').Album
  showUpload?: boolean
  onDelete?: (id: number) => Promise<void> | void
}>

// Componente responsável por listar os álbuns com paginação e busca
const Albums: React.FC = () => {

  // Estado com a lista de álbuns
  const [albums, setAlbums] = React.useState<Album[]>([])

  // Estado da página atual
  const [page, setPage] = React.useState(0)

  // Estado com o total de páginas
  const [totalPages, setTotalPages] = React.useState(0)

  // Estado do texto de busca
  const [q, setQ] = React.useState('')

  // Estado para controle de carregamento
  const [loading, setLoading] = React.useState(false)

  // Estado para armazenar mensagens de erro
  const [error, setError] = React.useState<string | null>(null)

  // Efeito executado ao mudar a página ou o termo de busca
  React.useEffect(() => {
    setLoading(true)
    setError(null)

    // Define qual serviço será utilizado (listagem ou busca)
    const load = q
      ? albumService.searchByName(q, page, PAGE_SIZE)
      : albumService.list(page, PAGE_SIZE)

    load
      .then(r => {
        // Atualiza lista de álbuns e total de páginas
        setAlbums(r.content)
        setTotalPages(r.totalPages)
      })
      .catch(e => setError((e as Error).message))
      .finally(() => setLoading(false))

  }, [page, q])

  // Mensagem de sucesso (em português) exibida após operações
  const [success, setSuccess] = React.useState<string | null>(null)

  return (
    <div className="site-container p-6">

      {/* Cabeçalho da página */}
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Álbuns</h1>
      </div>

      {/* Campo de busca */}
      <div className="mb-6 flex items-center gap-2">
        <input
          placeholder="Buscar por nome..."
          value={q}
          onChange={e => {
            setQ(e.target.value)
            setPage(0) // Reinicia a paginação ao buscar
          }}
          className="form-input"
        />
      </div>

      {/* Exibe loading */}
      {loading && (
        <div className="loading-screen">
          <div className="spinner" />
        </div>
      )}

      {/* Exibe erro */}
      {error && <div className="alert-danger">{error}</div>}

      {/* Mensagem de sucesso */}
      {success && <div className="alert-success">{success}</div>}
      {/* Grid de cards de álbuns */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {albums.map(a => (
          <div key={a.id}>

            {/* Suspense para carregamento lazy do card */}
            <React.Suspense
              fallback={<div className="text-gray-500">Carregando...</div>}
            >
              <AlbumCardLazy
                album={a}
                showUpload={false}
                onDelete={async (albumId: number) => {
                  try {
                    await albumService.del(albumId)
                    setAlbums(prev => prev.filter(p => p.id !== albumId))
                    setSuccess('Álbum excluído com sucesso')
                    setTimeout(() => setSuccess(null), 4000)
                  } catch (e: any) {
                    setError(e.message || 'Erro ao excluir álbum')
                  }
                }}
              />
            </React.Suspense>

          </div>
        ))}
      </div>

      {/* Paginação */}
      <Pagination
        page={page}
        totalPages={totalPages}
        onChange={p => setPage(p)}
      />

    </div>
  )
}

// Exporta o componente
export default Albums
