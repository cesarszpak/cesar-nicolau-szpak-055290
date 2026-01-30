import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { authFacade } from '../facades/auth.facade'

const Login: React.FC = () => {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function submit(e: React.FormEvent) {
    e.preventDefault()
    setLoading(true)
    setError(null)
    try {
      await authFacade.login(email, senha)
      navigate('/artistas')
    } catch (err: any) {
      setError(err.message || 'Erro ao efetuar login')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="bg-login">
      <form className="card-login" onSubmit={submit}>
        <h1 className="title-login">Bem vindo a página de login!</h1>

        {error && <div className="text-sm text-red-600 mb-2">{error}</div>}

        <div className="form-group-login">
          <label className="form-label-login">Email</label>
          <input
            className="form-input-login"
            type="email"
            placeholder="seu@email.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>

        <div className="form-group-login">
          <label className="form-label-login">Senha</label>
          <input
            className="form-input-login"
            type="password"
            placeholder="••••••••"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            required
          />
        </div>

        <div className="btn-group-login">
          <a className="link-login" href="#/forgot">Esqueceu a senha?</a>
          <button className="btn-primary" disabled={loading} type="submit">
            {loading ? 'Entrando...' : 'Entrar'}
          </button>
        </div>
      </form>
    </div>
  )
}

export default Login
