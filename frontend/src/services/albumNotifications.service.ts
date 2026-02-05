import { BehaviorSubject } from 'rxjs'

/**
 * Interface que representa a notificação de um novo álbum
 * recebida via WebSocket.
 */
export interface AlbumNotification {
  id: number
  nome: string
  artistaId: number
  artistaNome: string
  createdAt: string
}

/**
 * Serviço responsável por gerenciar a conexão WebSocket
 * e distribuir notificações de novos álbuns cadastrados.
 */
class AlbumNotificationsService {
  /**
   * Observable que emite notificações de álbuns.
   * Inicia com valor null até que a primeira mensagem seja recebida.
   */
  notifications$ = new BehaviorSubject<AlbumNotification | null>(null)

  /**
   * Instância do WebSocket utilizada para comunicação com a API.
   */
  private socket: WebSocket | null = null

  /**
   * Estabelece a conexão com o WebSocket da API.
   * Caso já exista uma conexão aberta, não faz nada.
   */
  connect() {
    // Evita criar múltiplas conexões WebSocket
    if (this.socket && this.socket.readyState === WebSocket.OPEN) return

    // Monta a URL do WebSocket usando a mesma origem do frontend.
    // Assim a conexão passa pelo proxy (Nginx) em desenvolvimento
    // e evitamos problemas de handshake/CORS entre navegadores.
    const url = (() => {
      // Se uma URL da API estiver configurada, utiliza-a (útil em dev remoto)
      if (import.meta.env.VITE_API_URL) {
        return import.meta.env.VITE_API_URL.replace(/^http/, 'ws') + '/ws/albums'
      }

      // Caso contrário, conecta na mesma origem da página (relativa)
      const proto = window.location.protocol === 'https:' ? 'wss' : 'ws'
      return `${proto}://${window.location.host}/ws/albums`
    })()

    // Cria a conexão WebSocket
    this.socket = new WebSocket(url)

    /**
     * Evento disparado quando a conexão é estabelecida com sucesso
     */
    this.socket.addEventListener('open', () => {
      console.debug('WebSocket de álbuns conectado em', url)
    })

    /**
     * Evento disparado quando uma mensagem é recebida do backend
     */
    this.socket.addEventListener('message', ev => {
      try {
        // Converte a mensagem recebida para JSON
        const data = JSON.parse(ev.data)

        // Emite a notificação para todos os inscritos
        this.notifications$.next(data)
      } catch (e) {
        console.error('Mensagem inválida recebida via WebSocket', e)
      }
    })

    /**
     * Evento disparado quando a conexão é encerrada
     * Tenta reconectar automaticamente após 5 segundos
     */
    this.socket.addEventListener('close', () => {
      console.debug(
        'WebSocket de álbuns desconectado. Tentando reconectar em 5 segundos...'
      )
      setTimeout(() => this.connect(), 5000)
    })

    /**
     * Evento disparado em caso de erro na conexão WebSocket
     */
    this.socket.addEventListener('error', err => {
      console.error('Erro no WebSocket de álbuns', err)

      // Garante o fechamento da conexão em caso de erro
      try {
        this.socket?.close()
      } catch (e) {
        console.error('Erro ao fechar o WebSocket após falha', e)
      }
    })
  }

  /**
   * Encerra a conexão WebSocket manualmente
   */
  disconnect() {
    try {
      this.socket?.close()
    } catch (e) {
      console.error('Erro ao desconectar o WebSocket de álbuns', e)
    }
    this.socket = null
  }
}

/**
 * Instância única do serviço de notificações de álbuns
 */
export const albumNotificationsService = new AlbumNotificationsService()
