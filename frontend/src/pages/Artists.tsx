import React from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { artistService, type Artist } from '../services/artist.service'
import ArtistsList from '../components/artists/ArtistsList'
import Pagination from '../components/Pagination'

/**
 * Quantidade de registros exibidos por página
 */
const PAGE_SIZE = 8

/**
 * Página de listagem de artistas.
 *
 * Responsável por buscar, filtrar, ordenar e paginar os artistas
 * consumindo os dados da API.
 */
const Artists: React.FC = () => {
  /**
   * Lista de artistas retornada da API
   */
  const [artists, setArtists] = React.useState<Artist[]>([])

  /**
   * Página atual (base 0)
   */
  const [page, setPage] = React.useState(0)

  /**
   * Total de páginas retornadas pela API
   */
  const [totalPages, setTotalPages] = React.useState(0)

  /**
   * Controle de estado de carregamento
   */
  const [loading, setLoading] = React.useState(false)

  /**
   * Texto de busca por nome do artista
   */
  const [q, setQ] = React.useState('')

  /**
   * Ordem de ordenação dos registros
   * asc = crescente
   * desc = decrescente
   */
  const [order, setOrder] = React.useState<'asc' | 'desc'>('asc')

  /**
   * Mensagem de erro retornada pela API
   */
  const [error, setError] = React.useState<string | null>(null)

  /**
   * Efeito responsável por carregar os artistas.
   *
   * Executado sempre que:
   * - a página mudar
   * - o termo de busca mudar
   * - a ordem de ordenação mudar
   */
  React.useEffect(() => {
    setError(null)
    setLoading(true)

    /**
     * Define qual chamada de serviço será utilizada:
     * - Busca por nome, caso exista termo de pesquisa
     * - Listagem padrão, caso contrário
     */
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
        // Finaliza o estado de carregamento
        setLoading(false)
      })
  }, [page, q, order])

  /**
   * Hooks de navegação e localização
   */
  const navigate = useNavigate()
  const location = useLocation()

  /**
   * Mensagem de sucesso vinda do state da navegação
   * (ex: após cadastro de um artista)
   */
  const [success, setSuccess] = React.useState<string | null>(
    location.state?.success ?? null
  )

  /**
   * Efeito responsável por:
   * - Exibir a mensagem de sucesso por tempo limitado
   * - Limpar o state do histórico para evitar reapresentação da mensagem
   */
  React.useEffect(() => {
    if (success) {
      const t = setTimeout(() => setSuccess(null), 4000)
      return () => clearTimeout(t)
    }

    // Limpa o state do histórico para que a mensagem
    // não seja exibida novamente ao voltar a página
    if (location.state && (location.state as any).success) {
      navigate(location.pathname, { replace: true, state: {} })
    }
  }, [success, location, navigate])

  return (
    <div className="p-6">
      {/* Cabeçalho da página */}
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Artistas</h1>

        {/* Ações da página (ex: botão para cadastrar novo artista) */}
        <div className="flex items-center space-x-2">
          <button
            onClick={() => navigate('/artistas/novo')}
            className="btn-primary"
          >
            Novo Artista
          </button>
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
              setPage(0) // Reseta para a primeira página ao realizar a busca
            }}
            className="form-input-login"
          />

          {/* Botão para alternar a ordem de listagem */}
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

      {/* Mensagem de sucesso */}
      {success && <div className="alert-success">{success}</div>}

      {/* Lista de artistas */}
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
