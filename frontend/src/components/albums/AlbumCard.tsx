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

// Importa ícones (Heroicons) para uso nos botões (Ver / Editar / Excluir)
// Comentários e textos em português conforme padrão do projeto
import { EyeIcon, PencilIcon, TrashIcon } from '@heroicons/react/24/solid'

// Componente responsável por exibir as informações resumidas de um álbum em formato de card
// Props:
// - album: dados do álbum a ser exibido
// - showUpload (opcional): suporte previsto, mas não utilizado neste componente
//   Por padrão é false para evitar exibir upload em listagens.
//   A tela de detalhes do álbum possui seu próprio formulário de upload.
const AlbumCard: React.FC<{ album: Album; showUpload?: boolean; onDelete?: (id: number) => Promise<void> | void }> = ({ album, onDelete }) => {

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
            className="btn-primary-md"
            title="Ver detalhes do álbum"
          >
            {/* Ícone do botão Ver (Eye) */}
            <EyeIcon className="h-4 w-4" aria-hidden="true" />
          </button>

          {/* Botão para editar álbum (classe btn-warning) */}
          <button
            onClick={() => navigate(`/albuns/${album.id}/editar`)}
            className="btn-warning-md"
            title="Editar álbum"
          >
            {/* Ícone do botão Editar (Pencil) */}
            <PencilIcon className="h-4 w-4" aria-hidden="true" />
          </button>

          {/* Botão para excluir álbum (classe btn-danger) */}
          <button
            onClick={async () => {
              const { confirmDelete } = await import('../../services/confirm.service')
              const ok = await confirmDelete(`o álbum ${album.nome}`)
              if (!ok) return

              try {
                // Se o componente pai passou onDelete, delega a exclusão para o pai
                // Isso evita que ocorra uma exclusão dupla (o que causa 404 "Álbum não encontrado")
                if (onDelete) {
                  await onDelete(album.id)
                  return
                }

                // Caso o pai não forneça onDelete, realiza a exclusão local e recarrega
                const { albumService } = await import('../../services/album.service')
                await albumService.del(album.id)

                // Mensagem de sucesso em português
                const Swal = (await import('sweetalert2')).default
                Swal.fire('Sucesso', 'Álbum excluído com sucesso', 'success')

                // Atualiza a página (fallback)
                window.location.reload()

              } catch (e: any) {
                // Exibe mensagem de erro em português
                const Swal = (await import('sweetalert2')).default
                Swal.fire('Erro', e?.message || 'Erro ao excluir álbum', 'error')
              }
            }}
            className="btn-danger-md"
            title="Excluir álbum"
          >
            {/* Ícone do botão Excluir (Trash) */}
            <TrashIcon className="h-4 w-4" aria-hidden="true" />
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
