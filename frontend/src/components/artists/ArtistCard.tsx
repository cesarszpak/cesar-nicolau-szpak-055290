import React from 'react' 
import type { Artist } from '../../services/artist.service'

// Componente responsável por exibir as informações de um artista
// Recebe um objeto do tipo Artist como propriedade
import Card from '../common/Card'
import { useNavigate } from 'react-router-dom'

const ArtistCard: React.FC<{ artist: Artist }> = ({ artist }) => {
  const navigate = useNavigate()

  return (
    // Card principal do artista
    <Card>

      {/* Cabeçalho do card com nome, quantidade de álbuns e ID */}
      <div className="flex items-center justify-between">
        <div>
          {/* Nome do artista */}
          <div className="text-lg font-semibold text-gray-900">
            {artist.nome}
          </div>

          {/* Quantidade de álbuns do artista */}
          <div className="text-sm text-gray-500">
            {artist.albumCount} álbum(ns)
          </div>
        </div>

        <div className="flex items-center gap-2">
          {/* Botão para visualizar artista e seus álbuns */}
          <button
            onClick={() => navigate(`/artistas/${artist.id}`)}
            className="btn-info"
          >
            Ver
          </button>

          {/* Identificador do artista */}
          <div className="text-sm text-gray-400">           
          </div>
        </div>
      </div>
    </Card>
  )
}

export default ArtistCard
