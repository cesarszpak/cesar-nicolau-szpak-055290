import { albumService, type Album } from '../services/album.service'
import { BehaviorSubject } from 'rxjs'

class AlbumFacade {
  albums$ = new BehaviorSubject<Album[]>([])
  totalPages$ = new BehaviorSubject<number>(0)

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
}

export const albumFacade = new AlbumFacade()