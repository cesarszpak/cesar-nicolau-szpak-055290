import { BehaviorSubject } from 'rxjs'
import api from './api'
import jwtDecode from 'jwt-decode'

/**
 * Estrutura esperada do payload do JWT
 */
interface TokenPayload {
  sub: string        // Identificador do usuário (normalmente o e-mail)
  exp?: number       // Timestamp de expiração do token (em segundos)
  nome?: string      // Nome do usuário
}

/**
 * Estrutura do usuário exposta para a aplicação
 */
export interface User {
  email: string
  nome?: string
}

/**
 * Serviço de autenticação
 * Responsável por login, refresh de token, logout
 * e gerenciamento do estado do usuário logado
 */
class AuthService {
  // BehaviorSubject mantém o último valor e emite para novos inscritos
  private currentUserSubject = new BehaviorSubject<User | null>(null)

  // Timers para expiração do token e refresh automático
  private tokenTimerId: number | null = null
  private refreshTimerId: number | null = null

  // Observable público para componentes reagirem ao estado do usuário
  user$ = this.currentUserSubject.asObservable()

  constructor() {
    // Recupera tokens do localStorage ao iniciar a aplicação
    const token = localStorage.getItem('token')
    const refresh = localStorage.getItem('refreshToken')

    // Se existir token, inicializa o usuário
    if (token) this.setToken(token)

    // Se não houver token mas existir refresh token, tenta renovar
    if (refresh) {
      if (!token) {
        this.refresh(refresh).catch(() => this.logout())
      }
    }
  }

  /**
   * Agenda a renovação automática do token
   * O refresh ocorre 30 segundos antes da expiração
   */
  private scheduleRefresh(exp: number) {
    const now = Date.now()
    const msUntil = exp * 1000 - now
    const refreshAt = Math.max(msUntil - 30_000, 0)

    // Cancela refresh anterior, se existir
    if (this.refreshTimerId) window.clearTimeout(this.refreshTimerId)

    this.refreshTimerId = window.setTimeout(() => {
      const refreshToken = localStorage.getItem('refreshToken')
      if (refreshToken) {
        this.refresh(refreshToken).catch(() => this.logout())
      }
    }, refreshAt)
  }

  /**
   * Salva o token, decodifica o JWT
   * e atualiza o estado do usuário
   */
  private setToken(token: string) {
    localStorage.setItem('token', token)

    // Decodifica o JWT para obter dados do usuário
    const payload = jwtDecode<TokenPayload>(token)

    const user: User = {
      email: payload.sub,
      nome: payload.nome
    }

    // Atualiza o usuário atual
    this.currentUserSubject.next(user)

    // Cancela timer anterior de expiração
    if (this.tokenTimerId) window.clearTimeout(this.tokenTimerId)

    if (payload.exp) {
      const expiresAt = payload.exp * 1000
      const now = Date.now()
      const ms = expiresAt - now

      // Força logout 1 segundo após a expiração do token
      this.tokenTimerId = window.setTimeout(() => {
        this.logout()
      }, Math.max(ms + 1000, 0))

      // Agenda refresh automático antes da expiração
      this.scheduleRefresh(payload.exp)
    }
  }

  /**
   * Registra um novo usuário no backend
   * Realiza login automático após o registro
   */
  async register(nome: string, email: string, senha: string) {
    // Envia requisição de criação de usuário ao backend
    await api.post('/api/usuarios', { nome, email, senha }, { authRequired: false })

    // Após o registro bem-sucedido, realiza login automático
    return this.login(email, senha)
  }

  /**
   * Realiza login no backend
   * Salva token e refresh token
   */
  async login(email: string, senha: string) {
    const res = await api.post('/login', { email, senha }, { authRequired: false })

    const token = res.token
    const refreshToken = res.refreshToken

    if (!token || !refreshToken) {
      throw new Error('Token não retornado')
    }

    localStorage.setItem('refreshToken', refreshToken)
    this.setToken(token)

    return this.currentUserSubject.getValue()
  }

  /**
   * Renova o token de acesso usando o refresh token
   */
  async refresh(refreshToken: string) {
    const res = await api.post('/refresh', refreshToken, { authRequired: false })

    if (!res || !res.token) {
      throw new Error('Refresh falhou')
    }

    const token = res.token
    const newRefresh = res.refreshToken

    localStorage.setItem('refreshToken', newRefresh)
    this.setToken(token)

    return this.currentUserSubject.getValue()
  }

  /**
   * Remove tokens, limpa timers
   * e encerra a sessão do usuário
   */
  logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')

    this.currentUserSubject.next(null)

    if (this.tokenTimerId) {
      window.clearTimeout(this.tokenTimerId)
      this.tokenTimerId = null
    }

    if (this.refreshTimerId) {
      window.clearTimeout(this.refreshTimerId)
      this.refreshTimerId = null
    }
  }

  /**
   * Retorna o usuário atual (snapshot)
   */
  getCurrentUser() {
    return this.currentUserSubject.getValue()
  }
}

// Instância única do serviço de autenticação
export const authService = new AuthService()
