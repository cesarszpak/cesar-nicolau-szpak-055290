import React, { type ReactElement } from 'react' 
import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router-dom'
import Login from './pages/Login'

// Importação lazy da página de artistas para carregamento sob demanda
const Artists = React.lazy(() => import('./pages/Artists'))
const ArtistCreate = React.lazy(() => import('./pages/ArtistCreate'))
const ArtistEdit = React.lazy(() => import('./pages/ArtistEdit'))
const ArtistView = React.lazy(() => import('./pages/ArtistView'))
const AlbumCreate = React.lazy(() => import('./pages/AlbumCreate'))
const AlbumEdit = React.lazy(() => import('./pages/AlbumEdit'))
const Albums = React.lazy(() => import('./pages/Albums'))
const AlbumView = React.lazy(() => import('./pages/AlbumView'))

import { authFacade } from './facades/auth.facade'
import SiteNav from './components/SiteNav'
import SafetyNetBanner from './components/SafetyNetBanner'
import './index.css'

const AlbumNotificationToast = React.lazy(() => import('./components/AlbumNotificationToast'))

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
  React.useEffect(() => {
    // Inicializa monitor proativo ao montar a aplicação (dynamic import evita problemas de build)
    let stop: (() => void) | undefined
    import('./services/healthMonitor').then(m => {
      if (m?.startProactiveMonitoring) stop = m.startProactiveMonitoring()
    }).catch(() => {
      // noop
    })

    return () => {
      if (stop) stop()
    }
  }, [])

  return (
    <BrowserRouter>

      {/* Menu exibido apenas para usuários autenticados */}
      <AuthNav />

      {/* Banner global de rede de segurança (aparece discretamente abaixo do menu) */}
      <React.Suspense fallback={null}>
        <SafetyNetBanner />
        <AlbumNotificationToast />
      </React.Suspense>

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

          {/* Rota para criar novo artista */}
          <Route
            path="/artistas/novo"
            element={
              <PrivateRoute>
                <ArtistCreate />
              </PrivateRoute>
            }
          />

          {/* Rota para editar artista */}
          <Route
            path="/artistas/:id/editar"
            element={
              <PrivateRoute>
                <ArtistEdit />
              </PrivateRoute>
            }
          />

          {/* Rota para visualizar artista e seus álbuns */}
          <Route
            path="/artistas/:id"
            element={
              <PrivateRoute>
                <ArtistView />
              </PrivateRoute>
            }
          />

          {/* Rotas de álbum (criar / editar) */}
          <Route
            path="/artistas/:id/albuns/novo"
            element={
              <PrivateRoute>
                <AlbumCreate />
              </PrivateRoute>
            }
          />

          <Route
            path="/artistas/:id/albuns/:albumId/editar"
            element={
              <PrivateRoute>
                <AlbumEdit />
              </PrivateRoute>
            }
          />

          {/* Página de álbuns */}
          <Route
            path="/albuns"
            element={
              <PrivateRoute>
                <Albums />
              </PrivateRoute>
            }
          />

          {/* Página de visualização de álbum */}
          <Route
            path="/albuns/:id"
            element={
              <PrivateRoute>
                <AlbumView />
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
