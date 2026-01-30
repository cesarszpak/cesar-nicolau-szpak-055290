import { authService, User } from '../services/auth.service'
import { Observable } from 'rxjs'

class AuthFacade {
  user$: Observable<User | null> = authService.user$

  login(email: string, password: string) {
    return authService.login(email, password)
  }

  logout() {
    authService.logout()
  }

  getCurrentUser() {
    return authService.getCurrentUser()
  }
}

export const authFacade = new AuthFacade()
