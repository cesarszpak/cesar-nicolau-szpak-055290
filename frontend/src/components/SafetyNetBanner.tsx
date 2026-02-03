import React from 'react'
// Funções responsáveis por monitorar o status do backend (subscribe escuta mudanças)
import { subscribe, getStatus } from '../services/backendMonitor'
// Instância configurada do axios para chamadas à API
import api from '../services/api'

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
    // Banner fixo no topo da tela avisando sobre indisponibilidade do serviço
    <div className="fixed top-0 left-0 right-0 bg-yellow-500 text-white p-3 z-50 shadow-md">
      <div className="max-w-5xl mx-auto flex items-center justify-between">
        <div>
          <strong>Serviço indisponível</strong>
          <div className="text-sm">
            O backend está indisponível no momento. Tentaremos reconectar automaticamente.
          </div>

          {/* Exibe o motivo da indisponibilidade, se existir */}
          {status.reason ? (
            <div className="text-xs opacity-80">{status.reason}</div>
          ) : null}
        </div>

        <div className="flex items-center gap-2">
          {/* Botão para forçar uma nova tentativa de conexão com o backend */}
          <button
            onClick={async () => {
              try {
                // Faz uma chamada manual ao endpoint de readiness
                await api.get('/actuator/probes/readiness')
              } catch (e) {
                // Nenhuma ação aqui: o monitor de backend cuidará da atualização
              }
            }}
            className="bg-white text-yellow-600 px-3 py-1 rounded shadow-sm text-sm"
          >
            Tentar reconectar
          </button>
        </div>
      </div>
    </div>
  )
}
