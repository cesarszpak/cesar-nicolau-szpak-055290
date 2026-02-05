import React from 'react'
import { useNavigate } from 'react-router-dom'
import { artistService } from '../services/artist.service'

/**
 * Página de cadastro de artista.
 *
 * Responsável por exibir o formulário e enviar os dados
 * para criação de um novo artista na API.
 */
const ArtistCreate: React.FC = () => {
  /**
   * Hook de navegação
   */
  const navigate = useNavigate()

  /**
   * Nome do artista informado no formulário
   */
  const [nome, setNome] = React.useState('')

  /**
   * Controle de estado de carregamento
   */
  const [loading, setLoading] = React.useState(false)

  /**
   * Mensagem de erro retornada pela API
   */
  const [error, setError] = React.useState<string | null>(null)

  /**
   * Submete o formulário de cadastro do artista.
   *
   * - Evita o comportamento padrão do formulário
   * - Envia os dados para a API
   * - Redireciona para a listagem em caso de sucesso
   */
  async function submit(e: React.FormEvent) {
    e.preventDefault()
    setLoading(true)
    setError(null)

    try {
      // Realiza o cadastro do artista
      await artistService.create({ nome })

      // Redireciona para a página de listagem com mensagem de sucesso
      navigate('/artistas', {
        state: { success: 'Artista criado com sucesso' },
      })
    } catch (err: any) {
      // Trata erro retornado pela API
      setError(err.message || 'Erro ao criar artista')
    } finally {
      // Finaliza o estado de carregamento
      setLoading(false)
    }
  }

  return (
    <div className="site-container p-6">
      {/* Título da página */}
      <h1 className="text-2xl font-bold mb-4">Novo Artista</h1>

      {/* Exibição de mensagem de erro */}
      {error && <div className="alert-danger">{error}</div>}

      {/* Formulário de cadastro */}
      <form onSubmit={submit} className="max-w-lg">
        <div className="mb-4">
          {/* Campo nome do artista */}
          <label htmlFor="nome" className="form-label">
            Nome*
          </label>

          <input
            id="nome"
            value={nome}
            onChange={e => setNome(e.target.value)}
            className="form-input"
            placeholder="Nome do artista"
            required
            minLength={2}
          />
        </div>

        {/* Ações do formulário */}
        <div className="one-input-one-button-per-line">
          {/* Botão de envio */}
          <button type="submit" disabled={loading} className="btn-success-md">
            {loading ? 'Salvando...' : 'Salvar'}
          </button>

          {/* Botão para cancelar e voltar */}
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

export default ArtistCreate
