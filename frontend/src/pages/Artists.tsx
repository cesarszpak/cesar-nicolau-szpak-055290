import React from 'react'
import { artistService, type Artist } from '../services/artist.service'
import ArtistsList from '../components/artists/ArtistsList'
import Pagination from '../components/Pagination'

/**
 * Quantidade de registros exibidos por página
 */
const PAGE_SIZE = 8

/**
 * Página de listagem de artistas
 * Responsável por buscar, filtrar, ordenar e paginar artistas
 */
const Artists: React.FC = () => {
  // Lista de artistas retornada da API
  const [artists, setArtists] = React.useState<Artist[]>([])

  // Página atual (base 0)
  const [page, setPage] = React.useState(0)

  // Total de páginas retornadas pela API
  const [totalPages, setTotalPages] = React.useState(0)

  // Controle de carregamento
  const [loading, setLoading] = React.useState(false)

  // Texto de busca por nome
  const [q, setQ] = React.useState('')

  // Ordem de ordenação (ascendente ou descendente)
  const [order, setOrder] = React.useState<'asc' | 'desc'>('asc')

  // Mensagem de erro
  const [error, setError] = React.useState<string | null>(null)

  /**
   * Efeito responsável por carregar os artistas
   * Executado sempre que página, busca ou ordem mudarem
   */
  React.useEffect(() => {
    setError(null)
    setLoading(true)

    // Define qual chamada será feita:
    // - Busca por nome, se existir termo
    // - Listagem padrão, caso contrário
    const load = q
      ? artistService.searchByName(q, order, page, PAGE_SIZE)
      : artistService.list(page, PAGE_SIZE)

    load
      .then(r => {
        // Atualiza a lista de artistas
        setArtists(r.content)

        // Atualiza o total de páginas
        setTotalPages(r.totalPages)
      })
      .catch(e => {
        // Trata erros da requisição
        setError((e as Error).message)
      })
      .finally(() => {
        // Finaliza o estado de loading
        setLoading(false)
      })
  }, [page, q, order])

  return (
    <div className="p-6">
      {/* Cabeçalho da página */}
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Artistas</h1>

        {/* Espaço reservado para futuras ações (ex: botão Novo Artista) */}
        <div className="flex items-center space-x-2">
        </div>
      </div>

      {/* Barra de busca e ordenação */}
      <div className="mb-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div className="flex items-center gap-2">
          {/* Campo de busca por nome */}
          <input
            type="search"
            placeholder="Buscar por nome..."
            value={q}
            onChange={e => {
              setQ(e.target.value)
              setPage(0) // Reseta para a primeira página ao buscar
            }}
            className="form-input-login"
          />

          {/* Botão para alternar a ordem */}
          <button
            onClick={() => setOrder(order === 'asc' ? 'desc' : 'asc')}
            className="btn-primary"
            title="Alternar ordem"
          >
            {order === 'asc' ? 'Crescente' : 'Decrescente'}
          </button>
        </div>

        {/* Indicador de página atual */}
        <div className="text-sm text-gray-600">
          Página {page + 1} de {totalPages || 1}
        </div>
      </div>

      {/* Indicador de carregamento */}
      {loading && (
        <div className="loading-screen">
          <div className="spinner" />
        </div>
      )}

      {/* Exibição de erro */}
      {error && <div className="alert-danger">{error}</div>}

      {/* Lista de artistas (somente se não houver erro) */}
      {!error && <ArtistsList artists={artists} />}

      {/* Componente de paginação */}
      <Pagination
        page={page}
        totalPages={totalPages}
        onChange={p => setPage(p)}
      />
    </div>
  )
}

export default Artists
