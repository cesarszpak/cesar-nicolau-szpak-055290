import api from './api'

export interface Album {
  id: number
  nome: string
  artistaId: number
  artistaNome?: string
  createdAt?: string
  updatedAt?: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

const list = (page = 0, size = 10) => api.get(`/api/albuns?page=${page}&size=${size}`) as Promise<Page<Album>>

const listByArtist = (artistaId: number, page = 0, size = 10) => api.get(`/api/albuns/artista/${artistaId}?page=${page}&size=${size}`) as Promise<Page<Album>>

const get = (id: number) => api.get(`/api/albuns/${id}`) as Promise<Album>

const searchByName = (nome: string, page = 0, size = 10) => api.get(`/api/albuns/buscar/nome?nome=${encodeURIComponent(nome)}&page=${page}&size=${size}`) as Promise<Page<Album>>

const create = (dto: { nome: string; artistaId: number }) => api.post('/api/albuns', dto) as Promise<Album>

const update = (id: number, dto: { nome: string; artistaId: number }) => api.put(`/api/albuns/${id}`, dto) as Promise<Album>

const del = (id: number) => api.del(`/api/albuns/${id}`)

export const albumService = { list, listByArtist, get, create, update, del, searchByName }