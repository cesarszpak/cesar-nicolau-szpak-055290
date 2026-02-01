import React from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { artistService, type Artist } from '../services/artist.service'
import { albumService, type Album } from '../services/album.service'

const PAGE_SIZE = 8

const ArtistView: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const artistId = Number(id)
  const navigate = useNavigate()

  const [artist, setArtist] = React.useState<Artist | null>(null)
  const [albums, setAlbums] = React.useState<Album[]>([])
  const [loading, setLoading] = React.useState(false)
  const [error, setError] = React.useState<string | null>(null)

  React.useEffect(() => {
    if (!artistId) return
    setLoading(true)
    setError(null)

    // Carrega artista e álbuns
    Promise.all([
      artistService.get(artistId),
      albumService.listByArtist(artistId, 0, PAGE_SIZE)
    ])
      .then(([a, albPage]) => {
        setArtist(a as Artist)
        setAlbums(albPage.content)
      })
      .catch(e => setError((e as Error).message))
      .finally(() => setLoading(false))
  }, [artistId])

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold">Visualizar Artista</h1>
          <div className="text-sm text-gray-600">Dados do artista e lista de álbuns</div>
        </div>
        <div>
          <button onClick={() => navigate(`/artistas/${artistId}/albuns/novo`)} className="btn-primary">
            Cadastrar Álbum
          </button>
        </div>
      </div>

      {loading && <div className="loading-screen"><div className="spinner"/></div>}
      {error && <div className="alert-danger">{error}</div>}

      {artist && (
        <div className="mb-6">
          <div className="text-xl font-semibold">{artist.nome}</div>
          <div className="text-sm text-gray-600">#{artist.id}</div>
        </div>
      )}

      <div>
        <h2 className="text-lg font-semibold mb-4">Álbuns</h2>
        {!albums.length && <div className="text-gray-500">Nenhum álbum encontrado</div>}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {albums.map(alb => (
            <div key={alb.id} className="card-album">
              <div className="flex items-center justify-between">
                <div>
                  <div className="font-semibold">{alb.nome}</div>
                  <div className="text-sm text-gray-500">#{alb.id}</div>
                </div>
                <div className="flex items-center gap-2">
                  <button onClick={() => navigate(`/artistas/${artistId}/albuns/${alb.id}/editar`)} className="btn-secondary">Editar</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

    </div>
  )
}

export default ArtistView