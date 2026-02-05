import React, { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { authFacade } from '../facades/auth.facade'

/**
 * Componente de Cadastro de Usuário
 * Responsável por permitir o registro de novos usuários
 * e redirecionar para a página de artistas após o cadastro
 */
const Register: React.FC = () => {
  // Hook de navegação do React Router
  const navigate = useNavigate()

  // Estados para armazenar os dados do formulário
  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [senhaConfirm, setSenhaConfirm] = useState('')

  // Estados de controle da interface
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  /**
   * Função executada ao submeter o formulário
   * Realiza o cadastro e trata estados de loading e erro
   */
  async function submit(e: React.FormEvent) {
    e.preventDefault() // Evita o reload da página

    // Validação: senhas devem ser iguais
    if (senha !== senhaConfirm) {
      setError('As senhas não correspondem')
      return
    }

    // Validação: senha mínima
    if (senha.length < 6) {
      setError('A senha deve ter pelo menos 6 caracteres')
      return
    }

    setLoading(true)
    setError(null)

    try {
      // Chama o facade de autenticação para registrar
      await authFacade.register(nome, email, senha)

      // Redireciona para a página de artistas após cadastro bem-sucedido
      navigate('/artistas')
    } catch (err: any) {
      // Define mensagem de erro para exibição
      setError(err.message || 'Erro ao efetuar cadastro')
    } finally {
      // Finaliza o estado de loading
      setLoading(false)
    }
  }

  return (
    <div className="bg-login">
      {/* Formulário de cadastro */}
      <form className="card-login" onSubmit={submit}>
        <h1 className="title-login">Criar nova conta</h1>

        {/* Exibe mensagem de erro, se existir */}
        {error && (
          <div className="alert-danger">
            {error}
          </div>
        )}

        {/* Campo de nome */}
        <div className="form-group-login">
          <label className="form-label-login">Nome completo</label>
          <input
            className="form-input-login"
            type="text"
            placeholder="Seu nome"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
            required
          />
        </div>

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
            placeholder="Mínimo 6 caracteres"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            required
          />
        </div>

        {/* Campo de confirmação de senha */}
        <div className="form-group-login">
          <label className="form-label-login">Confirmar Senha</label>
          <input
            className="form-input-login"
            type="password"
            placeholder="Confirme sua senha"
            value={senhaConfirm}
            onChange={(e) => setSenhaConfirm(e.target.value)}
            required
          />
        </div>

        {/* Ações do formulário */}
        <div className="btn-group-login">
          {/* Botão de submit com estado de loading */}
          <button className="btn-primary-md" disabled={loading} type="submit">
            {loading ? 'Criando conta...' : 'Cadastrar'}
          </button>
        </div>

        {/* Link para login */}
        <div style={{ marginTop: '1rem', textAlign: 'center' }}>
          <p className="form-label-login">
            Já tem uma conta?{' '}
            <Link to="/login" style={{ color: '#007bff', textDecoration: 'none', fontWeight: 'bold' }}>
              Fazer login
            </Link>
          </p>
        </div>
      </form>
    </div>
  )
}

export default Register
