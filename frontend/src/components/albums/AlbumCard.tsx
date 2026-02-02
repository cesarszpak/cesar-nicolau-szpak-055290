// Importa o React
import React from 'react'

// Importa o tipo Album
import type { Album } from '../../services/album.service'

// Importa o componente responsável por exibir as capas do álbum
import AlbumCovers from './AlbumCovers'

// Importa o componente Card reutilizável
import Card from '../common/Card'

// Importa o hook de navegação do React Router
import { useNavigate } from 'react-router-dom'

// Componente responsável por exibir as informações resumidas de um álbum em formato de card
// Props:
// - album: dados do álbum a ser exibido
// - showUpload (opcional): suporte previsto, mas não utilizado neste componente
//   Por padrão é false para evitar exibir upload em listagens.
//   A tela de detalhes do álbum possui seu próprio formulário de upload.
const AlbumCard: React.FC<{ album: Album; showUpload?: boolean }> = ({ album }) => {

  // Hook para navegação entre páginas
  const navigate = useNavigate()

  return (
    // Card que agrupa as informações do álbum
    <Card>

      {/* Cabeçalho do card com nome do álbum e ação */}
      <div className="flex items-start justify-between gap-2 mb-3">
        <div>
          {/* Nome do álbum */}
          <div className="font-semibold">{album.nome}</div>

          {/* Nome do artista */}
          <div className="text-sm text-gray-500">
            Artista: {album.artistaNome}
          </div>
        </div>

        {/* Botão para navegar para a tela de detalhes do álbum */}
        <div className="flex items-center gap-2">
          <button
            onClick={() => navigate(`/albuns/${album.id}`)}
            className="btn-info"
          >
            Ver
          </button>
        </div>
      </div>

      {/* Exibição das capas do álbum */}
      <div className="mb-3">
        <AlbumCovers albumId={album.id} />
      </div>

    </Card>
  )
}

// Exporta o componente
export default AlbumCard
