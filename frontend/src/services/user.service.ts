import api from './api'

/**
 * Estrutura de dados do usuário
 */
export interface User {
  id: number
  nome: string
  email: string
  createdAt?: string
  updatedAt?: string
}

/**
 * Estrutura de atualização de usuário (sem ID)
 */
export interface UserUpdateDTO {
  nome: string
  email: string
  senha?: string
}

/**
 * Serviço de usuários
 * Responsável por operações relacionadas ao perfil do usuário
 */
class UserService {
  /**
   * Obtém os dados do usuário atualmente autenticado
   *
   * @returns Dados do usuário autenticado
   */
  async getMe(): Promise<User> {
    return api.get('/api/me')
  }

  /**
   * Obtém os dados do usuário pelo ID
   *
   * @param id ID do usuário
   * @returns Dados do usuário
   */
  async getById(id: number): Promise<User> {
    return api.get(`/api/usuarios/${id}`)
  }

  /**
   * Atualiza os dados do usuário
   *
   * @param id ID do usuário
   * @param data Novos dados do usuário
   * @returns Usuário atualizado
   */
  async update(id: number, data: UserUpdateDTO): Promise<User> {
    return api.put(`/api/usuarios/${id}`, data)
  }
}

// Instância única do serviço de usuários
export const userService = new UserService()
