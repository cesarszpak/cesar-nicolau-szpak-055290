import { albumService, type Album } from '../services/album.service'
import { BehaviorSubject } from 'rxjs'

import { albumNotificationsService } from '../services/albumNotifications.service'

class AlbumFacade {
  albums$ = new BehaviorSubject<Album[]>([])
  totalPages$ = new BehaviorSubject<number>(0)

  // Notificações de novos álbuns
  notification$ = albumNotificationsService.notifications$

  async loadByArtist(artistaId: number, page = 0, size = 8) {
    const resp = await albumService.listByArtist(artistaId, page, size)
    this.albums$.next(resp.content)
    this.totalPages$.next(resp.totalPages)
    return resp
  }

  async search(nome: string, page = 0, size = 10) {
    const resp = await albumService.searchByName(nome, page, size)
    this.albums$.next(resp.content)
    this.totalPages$.next(resp.totalPages)
    return resp
  }

  // Inicializa a conexão de notificações (idempotente)
  startNotifications() {
    albumNotificationsService.connect()
  }
}

export const albumFacade = new AlbumFacade()