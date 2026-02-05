import { authService } from '../services/auth.service'
import type { User } from '../services/auth.service'
import { Observable } from 'rxjs'

class AuthFacade {
  user$: Observable<User | null> = authService.user$

  register(nome: string, email: string, senha: string) {
    return authService.register(nome, email, senha)
  }

  login(email: string, senha: string) {
    return authService.login(email, senha)
  }

  logout() {
    authService.logout()
  }

  getCurrentUser() {
    return authService.getCurrentUser()
  }
}

export const authFacade = new AuthFacade()
