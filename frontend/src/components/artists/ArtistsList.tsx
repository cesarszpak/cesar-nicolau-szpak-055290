import React from 'react' 
import type { Artist } from '../../services/artist.service'
import ArtistCard from './ArtistCard'

// Componente responsável por listar os artistas
// Recebe como propriedade um array de artistas
const ArtistsList: React.FC<{ artists: Artist[] }> = ({ artists }) => {

  // Caso o array de artistas esteja vazio, exibe uma mensagem informativa
  if (!artists.length) {
    return (
      <div className="text-center text-gray-500">
        Nenhum artista encontrado
      </div>
    )
  }

  return (
    // Container que organiza os cards dos artistas (grid)
    <div className="artist-grid">
      {/* Percorre a lista de artistas e renderiza um card para cada item */}
      {artists.map(a => (
        // A propriedade "key" é obrigatória para listas no React
        <ArtistCard key={a.id} artist={a} />
      ))}
    </div>
  )
}

export default ArtistsList
