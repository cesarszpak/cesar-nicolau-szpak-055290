import React from 'react'
import { albumFacade } from '../facades/album.facade'

/**
 * Componente responsável por exibir uma notificação (toast)
 * sempre que um novo álbum for cadastrado.
 */
export default function AlbumNotificationToast() {
  /**
   * Estado que armazena a notificação atual do álbum.
   * Quando for null, o toast não é exibido.
   */
  const [note, setNote] = React.useState<any | null>(null)

  /**
   * Efeito executado na montagem do componente.
   * Inicia o serviço de notificações e se inscreve
   * para receber eventos de novos álbuns.
   */
  React.useEffect(() => {
    // Inicia o monitoramento de notificações de álbuns
    albumFacade.startNotifications()

    // Inscrição no observable de notificações
    const sub = albumFacade.notification$.subscribe(n => {
      // Filtra apenas eventos de criação (tipo === 'CRIADO')
      // para evitar exibir o toast quando um álbum for apenas atualizado.
      if (n && (n as any).tipo === 'CRIADO') {
        // Atualiza o estado com a nova notificação
        setNote(n)

        // Remove o toast automaticamente após 8 segundos
        setTimeout(() => setNote(null), 8000)
      }
    })

    // Cancela a inscrição ao desmontar o componente
    return () => sub.unsubscribe()
  }, [])

  // Caso não exista notificação, não renderiza o componente
  if (!note) return null

  return (
    // Toast fixo no canto superior direito da tela
    <div className="fixed right-4 top-4 bg-indigo-600 text-white p-3 rounded shadow-md z-50">
      <div className="font-semibold">Novo álbum cadastrado</div>

      {/* Exibe o nome do álbum e do artista */}
      <div className="text-sm">
        {note.nome} — {note.artistaNome}
      </div>
    </div>
  )
}
