import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { userService, type User, type UserUpdateDTO } from '../services/user.service'
import { authFacade } from '../facades/auth.facade'

/**
 * Página de perfil do usuário
 * Permite visualizar e editar dados do usuário logado
 */
const UserProfile: React.FC = () => {
  // Hook de navegação
  const navigate = useNavigate()

  // Dados do usuário logado
  const currentUser = authFacade.getCurrentUser()
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)

  // Estados do formulário
  const [editMode, setEditMode] = useState(false)
  const [formData, setFormData] = useState<UserUpdateDTO>({
    nome: '',
    email: '',
    senha: ''
  })

  // Campos de senha (edição)
  const [confirmPassword, setConfirmPassword] = useState('')

  // Estados de validação
  const [validationErrors, setValidationErrors] = useState<Record<string, string>>({})

  /**
   * Carrega os dados do usuário ao montar o componente
   */
  React.useEffect(() => {
    if (!currentUser) {
      navigate('/login')
      return
    }

    // Carrega dados do usuário autenticado via endpoint /api/me
    loadUser()
  }, [currentUser, navigate])

  /**
   * Carrega os dados do usuário do backend
   */
  async function loadUser() {
    try {
      setLoading(true)
      setError(null)
      const userData = await userService.getMe()
      setUser(userData)
      setFormData({
        nome: userData.nome,
        email: userData.email
      })
    } catch (err: any) {
      setError(err.message || 'Erro ao carregar dados do usuário')
    } finally {
      setLoading(false)
    }
  }

  /**
   * Valida os dados do formulário
   */
  function validateForm(data: UserUpdateDTO): Record<string, string> {
    const errors: Record<string, string> = {}

    // Validação do nome
    if (!data.nome || data.nome.trim().length === 0) {
      errors.nome = 'Nome é obrigatório'
    } else if (data.nome.trim().length < 3) {
      errors.nome = 'Nome deve ter pelo menos 3 caracteres'
    } else if (data.nome.length > 100) {
      errors.nome = 'Nome não pode ter mais de 100 caracteres'
    }

    // Validação do email
    if (!data.email || data.email.trim().length === 0) {
      errors.email = 'Email é obrigatório'
    } else {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailRegex.test(data.email)) {
        errors.email = 'Email inválido'
      }
    }

    // Validação da senha (é exigida pelo backend no update)
    if (!data.senha || data.senha.trim().length === 0) {
      errors.senha = 'Senha é obrigatória'
    } else if (data.senha.length < 6) {
      errors.senha = 'Senha deve ter pelo menos 6 caracteres'
    } else if (data.senha.length > 255) {
      errors.senha = 'Senha não pode ter mais de 255 caracteres'
    }

    // Confirmação de senha
    if (data.senha && confirmPassword !== data.senha) {
      errors.confirmPassword = 'As senhas não coincidem'
    }

    return errors
  }

  /**
   * Alterna entre modo de visualização e edição
   */
  function toggleEditMode() {
    setEditMode(!editMode)
    setValidationErrors({})
    setSuccess(null)
    if (!editMode) {
      // Ao entrar no modo de edição, reseta os valores do formulário
      if (user) {
        setFormData({
          nome: user.nome,
          email: user.email
        })
        setConfirmPassword('')
      }
    }
  }

  /**
   * Atualiza o estado do formulário
   */
  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
    // Limpa erro deste campo ao editar
    if (validationErrors[name]) {
      setValidationErrors(prev => ({
        ...prev,
        [name]: ''
      }))
    }
  }

  function handlePasswordChange(e: React.ChangeEvent<HTMLInputElement>) {
    const { value } = e.target
    setFormData(prev => ({ ...prev, senha: value }))
    if (validationErrors.senha) {
      setValidationErrors(prev => ({ ...prev, senha: '' }))
    }
  }

  function handleConfirmPasswordChange(e: React.ChangeEvent<HTMLInputElement>) {
    setConfirmPassword(e.target.value)
    if (validationErrors.confirmPassword) {
      setValidationErrors(prev => ({ ...prev, confirmPassword: '' }))
    }
  }

  /**
   * Submete o formulário de atualização
   */
  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    
    // Valida o formulário
    const errors = validateForm(formData)
    if (Object.keys(errors).length > 0) {
      setValidationErrors(errors)
      return
    }

    if (!user) return

    try {
      setLoading(true)
      setError(null)
      // Monta payload contendo apenas os campos esperados pelo backend
      const payload: UserUpdateDTO = {
        nome: formData.nome,
        email: formData.email,
        senha: formData.senha || ''
      }

      const updated = await userService.update(user.id, payload)
      setUser(updated)
      setEditMode(false)
      setSuccess('Dados atualizados com sucesso!')
      
      // Remove a mensagem de sucesso após 3 segundos
      setTimeout(() => setSuccess(null), 3000)
    } catch (err: any) {
      setError(err.message || 'Erro ao atualizar dados')
    } finally {
      setLoading(false)
    }
  }

  if (loading && !user) {
    return (
      <div className="loading-screen">
        <div className="spinner" />
      </div>
    )
  }

  if (!user) {
    return (
      <div className="site-container p-6">
        <div className="alert-danger">
          Erro ao carregar dados do usuário
        </div>
      </div>
    )
  }

  return (
    <div className="site-container p-6">
      {/* Cabeçalho */}
      <div className="mb-6">
        <h1 className="text-2xl font-bold mb-2">Meu Perfil</h1>
        <p className="text-gray-600">Visualize e edite seus dados pessoais</p>
      </div>

      {/* Erros */}
      {error && (
        <div className="alert-danger mb-6">
          {error}
        </div>
      )}

      {/* Sucesso */}
      {success && (
        <div className="alert-success mb-6">
          {success}
        </div>
      )}

      {/* Card de perfil */}
      <div className="bg-white rounded-lg shadow p-6 max-w-2xl">
        {!editMode ? (
          // Modo de visualização
          <>
            <div className="mb-6">
              <div className="mb-4">
                <label className="text-sm font-semibold text-gray-700">Nome</label>
                <div className="mt-1 p-3 bg-gray-50 rounded border border-gray-200">
                  {user.nome}
                </div>
              </div>

              <div className="mb-4">
                <label className="text-sm font-semibold text-gray-700">Email</label>
                <div className="mt-1 p-3 bg-gray-50 rounded border border-gray-200">
                  {user.email}
                </div>
              </div>

              {user.createdAt && (
                <div className="mb-4">
                  <label className="text-sm font-semibold text-gray-700">Membro desde</label>
                  <div className="mt-1 p-3 bg-gray-50 rounded border border-gray-200">
                    {new Date(user.createdAt).toLocaleDateString('pt-BR')}
                  </div>
                </div>
              )}
            </div>

            {/* Botões */}
            <div className="flex gap-3">
              <button
                onClick={toggleEditMode}
                className="btn-primary-md"
              >
                Editar Dados
              </button>

              <button
                onClick={() => navigate('/artistas')}
                className="btn-secondary-md"
              >
                Voltar
              </button>
            </div>
          </>
        ) : (
          // Modo de edição
          <form onSubmit={handleSubmit}>
            <div className="mb-6">
              {/* Campo de nome */}
              <div className="mb-4">
                <label htmlFor="nome" className="form-label">
                  Nome
                </label>
                <input
                  id="nome"
                  name="nome"
                  type="text"
                  value={formData.nome}
                  onChange={handleChange}
                  className="form-input"
                  placeholder="Digite seu nome"
                  disabled={loading}
                />
                {validationErrors.nome && (
                  <p className="mt-1 text-sm text-red-500">{validationErrors.nome}</p>
                )}
              </div>

              {/* Campo de email */}
              <div className="mb-4">
                <label htmlFor="email" className="form-label">
                  Email
                </label>
                <input
                  id="email"
                  name="email"
                  type="email"
                  value={formData.email}
                  onChange={handleChange}
                  className="form-input"
                  placeholder="Digite seu email"
                  disabled={loading}
                />
                {validationErrors.email && (
                  <p className="mt-1 text-sm text-red-500">{validationErrors.email}</p>
                )}
              </div>
              {/* Campo de senha */}
              <div className="mb-4">
                <label htmlFor="senha" className="form-label">
                  Senha
                </label>
                <input
                  id="senha"
                  name="senha"
                  type="password"
                  value={formData.senha || ''}
                  onChange={handlePasswordChange}
                  className="form-input"
                  placeholder="Digite sua senha"
                  disabled={loading}
                />
                {validationErrors.senha && (
                  <p className="mt-1 text-sm text-red-500">{validationErrors.senha}</p>
                )}
              </div>

              {/* Campo confirmar senha */}
              <div className="mb-4">
                <label htmlFor="confirmPassword" className="form-label">
                  Confirmar Senha
                </label>
                <input
                  id="confirmPassword"
                  name="confirmPassword"
                  type="password"
                  value={confirmPassword}
                  onChange={handleConfirmPasswordChange}
                  className="form-input"
                  placeholder="Confirme sua senha"
                  disabled={loading}
                />
                {validationErrors.confirmPassword && (
                  <p className="mt-1 text-sm text-red-500">{validationErrors.confirmPassword}</p>
                )}
              </div>
            </div>

            {/* Botões */}
            <div className="flex gap-3">
              <button
                type="submit"
                disabled={loading}
                className="btn-success-md"
              >
                {loading ? 'Salvando...' : 'Salvar Alterações'}
              </button>

              <button
                type="button"
                onClick={toggleEditMode}
                disabled={loading}
                className="btn-secondary-md"
              >
                Cancelar
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  )
}

export default UserProfile
