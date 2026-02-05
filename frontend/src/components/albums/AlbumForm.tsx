// Importa o React
import React from 'react'

// Importa o serviço responsável pelas operações com álbuns
import { albumService } from '../../services/album.service'

// Importa hooks do React Router para navegação e leitura de parâmetros da rota
import { useNavigate, useParams } from 'react-router-dom'

// Componente responsável por criar ou editar um álbum
// Props:
// - initial (opcional): dados iniciais do álbum (usado principalmente na edição)
const AlbumForm: React.FC<{
  initial?: { nome: string; artistaId?: number }
}> = ({ initial }) => {

  // Hook para navegação entre páginas
  const navigate = useNavigate()

  // Parâmetros da rota
  const params = useParams()

  // Recupera o ID do artista a partir da rota
  const artistaIdFromRoute = Number(params.id ?? params.artistaId)

  // Recupera o ID do álbum (quando estiver em modo de edição)
  const albumId = params.albumId
    ? Number(params.albumId)
    : (params.id && params['albumId']
        ? Number(params['albumId'])
        : undefined)

  // Estado para o nome do álbum
  const [nome, setNome] = React.useState(initial?.nome ?? '')

  // Estado para o ID do artista (não é alterado no formulário)
  const [artistaId, _setArtistaId] = React.useState<number | undefined>(
    initial?.artistaId ?? artistaIdFromRoute
  )

  // Estado para controle de carregamento
  const [loading, setLoading] = React.useState(false)

  // Estado para armazenar mensagens de erro
  const [error, setError] = React.useState<string | null>(null)

  // Função executada no envio do formulário
  async function submit(e: React.FormEvent) {
    e.preventDefault()

    // Valida se o artista é válido
    if (!artistaId) return setError('Artista inválido')

    setLoading(true)

    try {
      if (albumId) {
        // Atualiza um álbum existente
        await albumService.update(albumId, { nome, artistaId })

        // Redireciona para a página do artista com mensagem de sucesso
        navigate(`/artistas/${artistaId}`, {
          state: { success: 'Álbum atualizado com sucesso' }
        })
      } else {
        // Cria um novo álbum
        await albumService.create({ nome, artistaId })

        // Redireciona para a página do artista com mensagem de sucesso
        navigate(`/artistas/${artistaId}`, {
          state: { success: 'Álbum criado com sucesso' }
        })
      }
    } catch (err: any) {
      // Captura e exibe erro
      setError(err.message || 'Erro')
    } finally {
      // Finaliza o loading
      setLoading(false)
    }
  }

  return (
    <div className="site-container p-6">

      {/* Título da página */}
      <h1 className="text-2xl font-bold mb-4">Novo Álbum</h1>

      {/* Exibe erro, se houver */}
      {error && <div className="alert-danger">{error}</div>}

      {/* Formulário de criação/edição */}
      <form onSubmit={submit} className="max-w-lg">

        {/* Campo nome do álbum */}
        <div className="mb-4">
          <label className="form-label">Nome*</label>
          <input
            required
            minLength={2}
            value={nome}
            onChange={e => setNome(e.target.value)}
            className="form-input"
          />
        </div>

        {/* Botões de ação */}
        <div className="one-input-one-button-per-line">
          <button
            type="submit"
            disabled={loading}
            className="btn-success-md"
          >
            {loading ? 'Salvando...' : 'Salvar'}
          </button>

          <button
            type="button"
            onClick={() => navigate(-1)}
            className="btn-secondary-md"
          >
            Cancelar
          </button>
        </div>

      </form>
    </div>
  )
}

// Exporta o componente
export default AlbumForm
