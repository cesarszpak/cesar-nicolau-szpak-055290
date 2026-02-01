import React from 'react'
import { albumService } from '../../services/album.service'
import { useNavigate, useParams } from 'react-router-dom'

const AlbumForm: React.FC<{ initial?: { nome: string; artistaId?: number } }> = ({ initial }) => {
  const navigate = useNavigate()
  const params = useParams()
  const artistaIdFromRoute = Number(params.id ?? params.artistaId)
  const albumId = params.albumId ? Number(params.albumId) : (params.id && params['albumId'] ? Number(params['albumId']) : undefined)

  const [nome, setNome] = React.useState(initial?.nome ?? '')
  const [artistaId, _setArtistaId] = React.useState<number | undefined>(initial?.artistaId ?? artistaIdFromRoute)
  const [loading, setLoading] = React.useState(false)
  const [error, setError] = React.useState<string | null>(null)

  async function submit(e: React.FormEvent) {
    e.preventDefault()
    if (!artistaId) return setError('Artista inválido')
    setLoading(true)
    try {
      if (albumId) {
        await albumService.update(albumId, { nome, artistaId })
        navigate(`/artistas/${artistaId}`, { state: { success: 'Álbum atualizado com sucesso' } })
      } else {
        await albumService.create({ nome, artistaId })
        navigate(`/artistas/${artistaId}`, { state: { success: 'Álbum criado com sucesso' } })
      }
    } catch (err: any) {
      setError(err.message || 'Erro')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Novo Álbum</h1>
      {error && <div className="alert-danger">{error}</div>}
      <form onSubmit={submit} className="max-w-lg">
        <div className="mb-4">
          <label className="form-label">Nome*</label>
          <input required minLength={2} value={nome} onChange={e => setNome(e.target.value)} className="form-input" />
        </div>

        <div className="one-input-one-button-per-line">
          <button type="submit" disabled={loading} className="btn-primary">{loading ? 'Salvando...' : 'Salvar'}</button>
          <button type="button" onClick={() => navigate(-1)} className="px-4 py-2 rounded border">Cancelar</button>
        </div>
      </form>
    </div>
  )
}

export default AlbumForm