import { authService } from '../services/auth.service'
import type { User } from '../services/auth.service'
import { Observable } from 'rxjs'

class AuthFacade {
  user$: Observable<User | null> = authService.user$

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
