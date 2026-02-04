import React from 'react' 
import { useNavigate, useParams } from 'react-router-dom'
import { artistService } from '../services/artist.service'

/**
 * Página para editar um artista existente.
 */
const ArtistEdit: React.FC = () => {
  const navigate = useNavigate()
  const params = useParams<{ id: string }>()
  const id = Number(params.id)

  // Estado para armazenar o nome do artista
  const [nome, setNome] = React.useState('')
  // Estado para controlar carregamento
  const [loading, setLoading] = React.useState(false)
  // Estado para mensagens de erro
  const [error, setError] = React.useState<string | null>(null)

  // Carrega os dados do artista ao montar o componente
  React.useEffect(() => {
    if (!id) return

    setLoading(true)
    artistService
      .get(id) // Chama o serviço para obter artista pelo ID
      .then(a => setNome(a.nome)) // Preenche o campo nome com os dados retornados
      .catch(e => setError(e.message || 'Erro ao carregar artista')) // Mensagem de erro
      .finally(() => setLoading(false)) // Finaliza o carregamento
  }, [id])

  /**
   * Função executada ao submeter o formulário.
   * Atualiza os dados do artista e navega para a lista de artistas.
   */
  async function submit(e: React.FormEvent) {
    e.preventDefault()
    setLoading(true)
    setError(null)

    try {
      await artistService.update(id, { nome }) // Atualiza artista via serviço
      navigate('/artistas', { state: { success: 'Artista atualizado com sucesso' } }) // Mensagem de sucesso 
    } catch (err: any) {
      setError(err.message || 'Erro ao atualizar artista') // Mensagem de erro
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="site-container p-6">
      <h1 className="text-2xl font-bold mb-4">Editar Artista</h1>

      {/* Exibe mensagem de erro, se houver */}
      {error && <div className="alert-danger">{error}</div>}

      <form onSubmit={submit} className="max-w-lg">
        <div className="mb-4">
          <label htmlFor="nome" className="form-label">
            Nome*
          </label>

          <input
            id="nome"
            value={nome}
            onChange={e => setNome(e.target.value)} // Atualiza estado ao digitar
            className="form-input"
            placeholder="Nome do artista"
            required
            minLength={2}
          />
        </div>

        <div className="one-input-one-button-per-line">
          {/* Botão de salvar com loading */}
          <button type="submit" disabled={loading} className="btn-primary">
            {loading ? 'Salvando...' : 'Salvar'}
          </button>

          {/* Botão de cancelar volta para a página anterior */}
          <button
            type="button"
            onClick={() => navigate(-1)}
            className="px-4 py-2 rounded border"
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  )
}

export default ArtistEdit
