import React from 'react'
import { useNavigate } from 'react-router-dom'
import { authService } from '../services/auth.service'

const Artists: React.FC = () => {
    const navigate = useNavigate()

    /**
     * Encerra a sessão do usuário
     * e redireciona para a tela de login
     */
    function handleLogout() {
        authService.logout()
        navigate('/login')
    }

    return (
        <div className="p-6">
            <h1>Listar os Artistas</h1>

            <button
                onClick={handleLogout}
                className="px-4 py-2 rounded bg-red-600 text-white hover:bg-red-700"
            >
                Sair
            </button>
        </div>
    )
}

export default Artists
