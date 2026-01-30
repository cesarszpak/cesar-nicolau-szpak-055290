import { BehaviorSubject } from 'rxjs'

export interface User {
  id: string
  email: string
}

class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null)

  user$ = this.currentUserSubject.asObservable()

  login(email: string, password: string) {
    // Placeholder: in real app call API
    const user: User = { id: '1', email }
    this.currentUserSubject.next(user)
    return Promise.resolve(user)
  }

  logout() {
    this.currentUserSubject.next(null)
  }

  getCurrentUser() {
    return this.currentUserSubject.getValue()
  }
}

export const authService = new AuthService()
