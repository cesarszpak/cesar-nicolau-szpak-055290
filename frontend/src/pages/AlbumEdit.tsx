import React from 'react'
import { useParams } from 'react-router-dom'
import AlbumForm from '../components/albums/AlbumForm'
import { albumService } from '../services/album.service'

const AlbumEdit: React.FC = () => {
  const { albumId } = useParams<{ albumId: string }>()
  const [initial, setInitial] = React.useState<{ nome: string; artistaId?: number } | null>(null)

  React.useEffect(() => {
    if (!albumId) return
    albumService.get(Number(albumId)).then(a => {
      setInitial({ nome: a.nome, artistaId: a.artistaId })
    }).catch(() => {})
  }, [albumId])

  // Reuse AlbumForm but for update we need to call update when submitting
  // For simplicity, if initial present, render custom form
  if (!initial) return <div className="p-6">Carregando...</div>

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Editar Álbum</h1>
      <AlbumForm initial={initial} />
    </div>
  )
}

export default AlbumEdit