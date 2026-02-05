import React from 'react' 
import type { Artist } from '../../services/artist.service'

// Componente responsável por exibir as informações de um artista
// Recebe um objeto do tipo Artist como propriedade
import Card from '../common/Card'
import { useNavigate } from 'react-router-dom'

// Importa ícones (Heroicons) para uso nos botões do card de artista
import { EyeIcon, PencilIcon, TrashIcon } from '@heroicons/react/24/solid' 

const ArtistCard: React.FC<{ artist: Artist; onDelete?: (id: number) => void }> = ({ artist, onDelete }) => {
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
            className="btn-primary-md"
            title="Ver detalhes do artista"
          >
            {/* Ícone do botão Ver (Eye) */}
            <EyeIcon className="h-4 w-4" aria-hidden="true" />
            {/* Ver */}
          </button>

          {/* Botão para editar artista (classe btn-warning) */}
          <button
            onClick={() => navigate(`/artistas/${artist.id}/editar`)}
            className="btn-warning-md"
            title="Editar artista"
          >
            {/* Ícone do botão Editar (Pencil) */}
            <PencilIcon className="h-4 w-4" aria-hidden="true" />
            {/* Editar */}
          </button>

          {/* Botão para excluir artista (classe btn-danger) */}
          <button
            onClick={async () => {
              // Usa o serviço de confirmação (SweetAlert2) para confirmação em português
              const { confirmDelete } = await import('../../services/confirm.service')
              const ok = await confirmDelete(`o artista ${artist.nome}`)
              if (!ok) return

              // Se foi passado o callback de onDelete, chama-o
              if (onDelete) onDelete(artist.id)
            }}
            className="btn-danger-md"
            title="Excluir artista"
          >
            {/* Ícone do botão Excluir (Trash) */}
            <TrashIcon className="h-4 w-4" aria-hidden="true" />
            {/* Excluir */}
          </button>

        </div>
      </div>
    </Card>
  )
}

export default ArtistCard
