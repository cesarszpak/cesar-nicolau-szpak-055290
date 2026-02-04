import React from 'react'
// Funções responsáveis por monitorar o status do backend (subscribe escuta mudanças)
import { subscribe, getStatus } from '../services/backendMonitor'

/**
 * Componente responsável por exibir um banner de aviso
 * quando o backend estiver indisponível (status diferente de UP).
 */
export default function SafetyNetBanner() {
  // Estado local que armazena o status atual do backend
  const [status, setStatus] = React.useState(getStatus())

  // Efeito que se inscreve no monitor de backend ao montar o componente
  React.useEffect(() => {
    // Atualiza o estado sempre que o status do backend mudar
    const unsub = subscribe((s, reason) => setStatus({ status: s, reason }))

    // Remove a inscrição ao desmontar o componente
    return () => {
      unsub()
    }
  }, [])

  // Se o backend estiver OK, não renderiza nada
  if (status.status === 'UP') return null

  return (
    // Barra discreta posicionada abaixo do menu (top-16 assume altura do header)
    <div className="fixed top-16 left-0 right-0 bg-yellow-500 text-white py-2 z-40 shadow-sm">
      <div className="max-w-5xl mx-auto text-center text-sm font-semibold">
        Serviço indisponível
        {/* Exibe o motivo brevemente se for "Rate limit exceeded" */}
        {status.reason ? (
          <span className="ml-2 text-xs opacity-90">{status.reason}</span>
        ) : null}
      </div>
    </div>
  )
}
