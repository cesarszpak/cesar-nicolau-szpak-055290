import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { authFacade } from '../facades/auth.facade'

/**
 * Componente de Login
 * Responsável por autenticar o usuário e redirecionar
 * para a página de artistas após o login
 */
const Login: React.FC = () => {
  // Hook de navegação do React Router
  const navigate = useNavigate()

  // Estados para armazenar os dados do formulário
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')

  // Estados de controle da interface
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  /**
   * Função executada ao submeter o formulário
   * Realiza o login e trata estados de loading e erro
   */
  async function submit(e: React.FormEvent) {
    e.preventDefault() // Evita o reload da página

    setLoading(true)
    setError(null)

    try {
      // Chama o facade de autenticação
      await authFacade.login(email, senha)

      // Redireciona para a página de artistas após login bem-sucedido
      navigate('/artistas')
    } catch (err: any) {
      // Define mensagem de erro para exibição
      setError(err.message || 'Erro ao efetuar login')
    } finally {
      // Finaliza o estado de loading
      setLoading(false)
    }
  }

  return (
    <div className="bg-login">
      {/* Formulário de login */}
      <form className="card-login" onSubmit={submit}>
        <h1 className="title-login">Bem vindo a página de login!</h1>

        {/* Exibe mensagem de erro, se existir */}
        {error && (
          <div className="text-sm text-red-600 mb-2">
            {error}
          </div>
        )}

        {/* Campo de e-mail */}
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

        {/* Campo de senha */}
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

        {/* Ações do formulário */}
        <div className="btn-group-login">
          {/* Link para recuperação de senha */}
          <a className="link-login" href="#/forgot">
            Esqueceu a senha?
          </a>

          {/* Botão de submit com estado de loading */}
          <button className="btn-primary" disabled={loading} type="submit">
            {loading ? 'Entrando...' : 'Entrar'}
          </button>
        </div>
      </form>
    </div>
  )
}

export default Login
