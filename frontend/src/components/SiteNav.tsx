import React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authService } from '../services/auth.service'

/**
 * Componente de navegação principal do site
 * Exibe links, logo e opção de logout
 * Possui versão desktop e mobile (menu responsivo)
 */
const SiteNav: React.FC = () => {
  // Hook para navegação programática
  const navigate = useNavigate()

  // Controle de abertura do menu mobile
  const [open, setOpen] = React.useState(false)

  /**
   * Realiza logout do usuário
   * Remove tokens e redireciona para a página de login
   */
  function handleLogout() {
    authService.logout()
    navigate('/login')
  }

  return (
    <nav className="site-nav">
      {/* Container principal da barra de navegação */}
      <div className="site-container flex items-center justify-between h-16">
        
        {/* Logo e links principais */}
        <div className="flex items-center">
          <Link to="/" className="inline-flex items-center">
            <div className="site-brand"/>
            <div className="site-brand-text">Sistema</div>
          </Link>

          {/* Links exibidos apenas em telas maiores */}
          <div className="nav-links">
            <Link to="/" className="nav-link">Home</Link>
            <Link to="/albuns" className="nav-link">Álbuns</Link>
          </div>
        </div>

        {/* Botão de sair (desktop) */}
        <div className="hidden sm:flex sm:items-center">
          <button onClick={handleLogout} className="btn-primary">
            Sair
          </button>
        </div>

        {/* Botão de menu hamburguer (mobile) */}
        <div className="-mr-2 flex items-center sm:hidden">
          <button
            aria-expanded={open}
            aria-label="Abrir menu"
            onClick={() => setOpen(!open)}
            className="p-2 rounded-md text-gray-200 hover:text-white hover:bg-[#12245c]"
          >
            ☰
          </button>
        </div>
      </div>

      {/* Menu mobile */}
      <div className={`sm:hidden ${open ? '' : 'hidden'}`}>
        <div className="pt-2 pb-3 space-y-1 bg-[#183181]">
          <Link
            to="/"
            className="block pl-3 pr-4 py-2 text-base font-semibold text-white hover:bg-[#12245c]"
          >
            Home
          </Link>

          <Link
            to="/albuns"
            className="block pl-3 pr-4 py-2 text-base font-semibold text-white hover:bg-[#12245c]"
          >
            Álbuns
          </Link>

          {/* Botão de sair no menu mobile */}
          <button
            onClick={handleLogout}
            className="w-full text-left pl-3 pr-4 py-2 text-base font-semibold text-white hover:bg-[#12245c]"
          >
            Sair
          </button>
        </div>
      </div>
    </nav>
  )
}

export default SiteNav
