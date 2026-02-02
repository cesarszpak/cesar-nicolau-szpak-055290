// Importa o React
import React from 'react'

// Importa hook para acessar parâmetros da rota
import { useParams } from 'react-router-dom'

// Importa o formulário reutilizável de álbum
import AlbumForm from '../components/albums/AlbumForm'

// Importa o serviço responsável pelas operações com álbuns
import { albumService } from '../services/album.service'

// Componente responsável pela edição de um álbum
const AlbumEdit: React.FC = () => {

  // Recupera o parâmetro albumId da rota
  const { albumId } = useParams<{ albumId: string }>()

  // Estado para armazenar os dados iniciais do álbum
  const [initial, setInitial] = React.useState<{
    nome: string
    artistaId?: number
  } | null>(null)

  // Efeito executado ao carregar o componente ou quando o albumId muda
  React.useEffect(() => {
    if (!albumId) return

    // Busca os dados do álbum para edição
    albumService.get(Number(albumId))
      .then(a => {
        // Define os dados iniciais do formulário
        setInitial({
          nome: a.nome,
          artistaId: a.artistaId
        })
      })
      .catch(() => {
        // Erro ignorado propositalmente (pode ser tratado futuramente)
      })
  }, [albumId])

  // Enquanto os dados iniciais não forem carregados, exibe mensagem de loading
  if (!initial) {
    return (
      <div className="site-container p-6">
        Carregando...
      </div>
    )
  }

  return (
    <div className="site-container p-6">

      {/* Título da página */}
      <h1 className="text-2xl font-bold mb-4">
        Editar Álbum
      </h1>

      {/* Reutiliza o formulário de álbum em modo de edição */}
      <AlbumForm initial={initial} />
    </div>
  )
}

// Exporta o componente
export default AlbumEdit
