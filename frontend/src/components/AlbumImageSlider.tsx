import React from 'react'
import type { CapaAlbumDTO } from '../services/capa.service'
import { capaService } from '../services/capa.service'
import ImageSlider from './ImageSlider'

/**
 * Componente que exibe as capas de um álbum em um slider automático.
 * 
 * Utiliza o componente ImageSlider reutilizável para exibir
 * as imagens com navegação automática e manual.
 */
const AlbumImageSlider: React.FC<{ albumId: number }> = ({ albumId }) => {
  // Estado com a lista de capas do álbum
  const [covers, setCover] = React.useState<CapaAlbumDTO[]>([])
  
  // Estado para controle de carregamento
  const [loading, setLoading] = React.useState(false)
  
  // Estado para armazenar mensagens de erro
  const [error, setError] = React.useState<string | null>(null)

  // Carrega as capas ao montar o componente
  React.useEffect(() => {
    setLoading(true)
    setError(null)

    capaService.listByAlbum(albumId)
      .then((r: CapaAlbumDTO[]) => setCover(r))
      .catch((e: Error) => setError(e.message))
      .finally(() => setLoading(false))
  }, [albumId])

  // Exibe estado de carregamento
  if (loading) {
    return (
      <div className="w-full h-96 bg-gray-100 rounded flex items-center justify-center">
        <div className="spinner" />
      </div>
    )
  }

  // Exibe erro, se houver
  if (error) {
    return (
      <div className="w-full bg-red-50 border border-red-200 rounded p-4 text-red-700">
        Erro ao carregar imagens: {error}
      </div>
    )
  }

  // Se não há capas, exibe mensagem
  if (covers.length === 0) {
    return (
      <div className="w-full h-96 bg-gray-100 rounded flex items-center justify-center">
        <p className="text-gray-500">Nenhuma imagem disponível para este álbum</p>
      </div>
    )
  }

  // Converte as capas para o formato esperado pelo ImageSlider
  const sliderImages = covers.map(cap => ({
    id: cap.id,
    // Em vez de confiar no campo `url` (pode não estar presente),
    // utilizamos o endpoint interno que serve o conteúdo da capa.
    // Isso garante que o slider carregue a imagem corretamente.
    url: `/api/capas/${cap.id}/conteudo`,
    title: cap.nomeArquivo
  }))

  return (
    <ImageSlider
      images={sliderImages}
      autoplayInterval={5000}
      autoplay={true}
      imageClassName="w-full h-96 object-cover rounded-lg"
    />
  )
}

export default AlbumImageSlider
