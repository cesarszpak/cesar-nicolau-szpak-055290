import React, { type ReactElement } from 'react' 
import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router-dom'
import Login from './pages/Login'

// Importação lazy da página de artistas para carregamento sob demanda
const Artists = React.lazy(() => import('./pages/Artists'))

import { authFacade } from './facades/auth.facade'
import SiteNav from './components/SiteNav'
import './index.css'

// Componente responsável por proteger rotas privadas
// Redireciona para o login caso o usuário não esteja autenticado
function PrivateRoute({ children }: { children: ReactElement }) {
  const user = authFacade.getCurrentUser()

  // Se não existir usuário autenticado, redireciona para /login
  if (!user) return <Navigate to="/login" replace />

  // Caso esteja autenticado, renderiza o conteúdo da rota
  return children
}

// Componente que observa mudanças no estado de autenticação
// Caso o usuário seja deslogado, redireciona automaticamente para o login
function AuthWatcher() {
  const navigate = useNavigate()

  React.useEffect(() => {
    // Inscreve-se no observable de usuário
    const sub = authFacade.user$.subscribe(user => {
      if (!user) navigate('/login')
    })

    // Remove a inscrição ao desmontar o componente
    return () => sub.unsubscribe()
  }, [navigate])

  return null
}

// Componente responsável por exibir a navegação apenas quando autenticado
function AuthNav() {
  const [user, setUser] = React.useState(authFacade.getCurrentUser())

  React.useEffect(() => {
    // Atualiza o estado local ao observar mudanças no usuário autenticado
    const sub = authFacade.user$.subscribe(u => setUser(u))
    return () => sub.unsubscribe()
  }, [])

  // Não renderiza o menu se não houver usuário autenticado
  if (!user) return null

  return <SiteNav />
}

// Componente principal da aplicação
function App() {
  return (
    <BrowserRouter>

      {/* Menu exibido apenas para usuários autenticados */}
      <AuthNav />

      {/* Observador global de autenticação */}
      <AuthWatcher />

      {/* Suspense para carregamento das páginas lazy */}
      <React.Suspense fallback={<div className="p-6">Carregando...</div>}>
        <Routes>

          {/* Rota pública de login */}
          <Route path="/login" element={<Login />} />

          {/* Rota protegida de artistas */}
          <Route
            path="/artistas"
            element={
              <PrivateRoute>
                <Artists />
              </PrivateRoute>
            }
          />

          {/* Redirecionamento da raiz para a página de artistas */}
          <Route path="/" element={<Navigate to="/artistas" replace />} />

        </Routes>
      </React.Suspense>
    </BrowserRouter>
  )
}

export default App
