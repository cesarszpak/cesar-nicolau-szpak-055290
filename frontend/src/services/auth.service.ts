import { BehaviorSubject } from 'rxjs'
import api from './api'
import jwtDecode from 'jwt-decode'

interface TokenPayload {
  sub: string
  exp?: number
  nome?: string
}

export interface User {
  email: string
  nome?: string
}

class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null)
  private tokenTimerId: number | null = null

  user$ = this.currentUserSubject.asObservable()

  constructor() {
    const token = localStorage.getItem('token')
    if (token) this.setToken(token)
  }

  private setToken(token: string) {
    localStorage.setItem('token', token)
    const payload = jwtDecode<TokenPayload>(token)
    const user: User = { email: payload.sub, nome: payload.nome }
    this.currentUserSubject.next(user)

    // clear previous timer
    if (this.tokenTimerId) window.clearTimeout(this.tokenTimerId)

    if (payload.exp) {
      const expiresAt = payload.exp * 1000
      const now = Date.now()
      const ms = expiresAt - now
      // logout 1s after expiration
      this.tokenTimerId = window.setTimeout(() => {
        this.logout()
        // Optionally: emit event or notify user
      }, Math.max(ms + 1000, 0))
    }
  }

  async login(email: string, senha: string) {
    const res = await api.post('/login', { email, senha }, { auth: false })
    const token = res.token
    if (!token) throw new Error('Token não retornado')
    this.setToken(token)
    return this.currentUserSubject.getValue()
  }

  logout() {
    localStorage.removeItem('token')
    this.currentUserSubject.next(null)
    if (this.tokenTimerId) {
      window.clearTimeout(this.tokenTimerId)
      this.tokenTimerId = null
    }
  }

  getCurrentUser() {
    return this.currentUserSubject.getValue()
  }
}

export const authService = new AuthService()
