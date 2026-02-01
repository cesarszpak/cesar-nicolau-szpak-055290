import React from 'react' 
import type { Artist } from '../../services/artist.service'

// Componente responsável por exibir as informações de um artista
// Recebe um objeto do tipo Artist como propriedade
const ArtistCard: React.FC<{ artist: Artist }> = ({ artist }) => {
  return (
    // Card principal do artista
    <div className="card-artist">

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

        {/* Identificador do artista */}
        <div className="text-sm text-gray-400">
          #{artist.id}
        </div>
      </div>
    </div>
  )
}

export default ArtistCard
