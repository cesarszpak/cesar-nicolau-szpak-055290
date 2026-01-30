import React, { type ReactElement } from 'react'
import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router-dom'
import Login from './pages/Login'
import Artists from './pages/Artists'
import { authFacade } from './facades/auth.facade'
import './index.css'

function PrivateRoute({ children }: { children: ReactElement }) {
  const user = authFacade.getCurrentUser()
  if (!user) return <Navigate to="/login" replace />
  return children
}

function AuthWatcher() {
  const navigate = useNavigate()
  React.useEffect(() => {
    const sub = authFacade.user$.subscribe(user => {
      if (!user) navigate('/login')
    })
    return () => sub.unsubscribe()
  }, [navigate])
  return null
}

function App() {
  return (
    <BrowserRouter>
      <AuthWatcher />
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route
          path="/artistas"
          element={<PrivateRoute><Artists /></PrivateRoute>}
        />
        <Route path="/" element={<Navigate to="/artistas" replace />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
