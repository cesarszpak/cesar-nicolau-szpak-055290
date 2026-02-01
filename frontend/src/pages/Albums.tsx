import React from 'react'
import { albumService, type Album } from '../services/album.service'
import Pagination from '../components/Pagination'

const PAGE_SIZE = 10

const Albums: React.FC = () => {
  const [albums, setAlbums] = React.useState<Album[]>([])
  const [page, setPage] = React.useState(0)
  const [totalPages, setTotalPages] = React.useState(0)
  const [q, setQ] = React.useState('')
  const [loading, setLoading] = React.useState(false)
  const [error, setError] = React.useState<string | null>(null)

  React.useEffect(() => {
    setLoading(true)
    setError(null)
    const load = q ? albumService.searchByName(q, page, PAGE_SIZE) : albumService.list(page, PAGE_SIZE)
    load.then(r => {
      setAlbums(r.content)
      setTotalPages(r.totalPages)
    }).catch(e => setError((e as Error).message)).finally(() => setLoading(false))
  }, [page, q])

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Álbuns</h1>
      </div>

      <div className="mb-6 flex items-center gap-2">
        <input placeholder="Buscar por nome..." value={q} onChange={e => { setQ(e.target.value); setPage(0) }} className="form-input" />
      </div>

      {loading && <div className="loading-screen"><div className="spinner"/></div>}
      {error && <div className="alert-danger">{error}</div>}

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {albums.map(a => (
          <div key={a.id} className="card-album">
            <div className="font-semibold">{a.nome}</div>
            <div className="text-sm text-gray-500">Artista: {a.artistaNome} #{a.artistaId}</div>
          </div>
        ))}
      </div>

      <Pagination page={page} totalPages={totalPages} onChange={p => setPage(p)} />
    </div>
  )
}

export default Albums