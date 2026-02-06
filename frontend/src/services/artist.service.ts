import api from './api' 

// Interface que representa um artista retornado pela API
export interface Artist {
  id: number
  nome: string
  albumCount: number
}

// Interface genérica para respostas paginadas
export interface Page<T> {
  // Lista de registros da página atual
  content: T[]

  // Total de registros encontrados
  totalElements: number

  // Total de páginas disponíveis
  totalPages: number

  // Quantidade de registros por página
  size: number

  // Número da página atual (base 0)
  number: number
}

// Recupera a lista de artistas com paginação
const list = (page = 0, size = 9) =>
  api.get(
    `/api/artistas?page=${page}&size=${size}`
  ) as Promise<Page<Artist>>

// Busca artistas pelo nome com ordenação e paginação
const searchByName = (
  nome: string,
  ordem: 'asc' | 'desc' = 'asc',
  page = 0,
  size = 9
) =>
  api.get(
    `/api/artistas/buscar/nome?nome=${encodeURIComponent(nome)}&ordem=${ordem}&page=${page}&size=${size}`
  ) as Promise<Page<Artist>>

// Cria um novo artista
const create = (dto: { nome: string }) =>
  api.post('/api/artistas', dto) as Promise<Artist>

const get = (id: number) => api.get(`/api/artistas/${id}`) as Promise<Artist>

// Atualiza um artista existente
const update = (id: number, dto: { nome: string }) =>
  api.put(`/api/artistas/${id}`, dto) as Promise<Artist>

// Exclui um artista
const remove = (id: number) => api.del(`/api/artistas/${id}`)

// Exporta os métodos do serviço de artistas
export const artistService = { list, searchByName, create, get, update, remove }
