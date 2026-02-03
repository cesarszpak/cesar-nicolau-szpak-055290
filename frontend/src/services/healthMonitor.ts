import api from './api'
import { setDown, setUp } from './backendMonitor'

/**
 * Identificador do timer utilizado para o monitoramento periódico
 */
let timer: number | undefined

/**
 * Inicia o monitoramento proativo do backend.
 *
 * Realiza chamadas periódicas ao endpoint de readiness para verificar
 * se o serviço está disponível. Caso esteja UP, atualiza o estado para UP;
 * caso contrário ou em caso de erro de rede, marca como DOWN.
 *
 * @param intervalMs Intervalo entre as verificações, em milissegundos (padrão: 15s)
 * @returns Função para interromper o monitoramento
 */
export function startProactiveMonitoring(intervalMs = 15000) {
  /**
   * Executa a checagem de readiness do backend
   */
  async function check() {
    try {
      const res = await api.get('/actuator/probes/readiness')

      // A estrutura da resposta pode variar; tenta extrair o status
      const status =
        res?.status ||
        res?.Status ||
        res?.statusCode ||
        res?.status ||
        (res && res)

      // Caso a resposta seja um objeto no formato { status: 'UP' }
      const s = (typeof res === 'object' && res?.status) ? res.status : null

      if (s === 'UP' || status === 'UP') {
        // Backend disponível
        setUp()
      } else {
        // Backend respondeu, mas não está pronto
        setDown('readiness not UP')
      }
    } catch (e: any) {
      // Erro de rede ou falha ao acessar o backend
      setDown(e?.message || 'erro de rede')
    }
  }

  // Executa a checagem imediatamente e agenda as próximas execuções
  check()
  timer = window.setInterval(check, intervalMs)

  // Retorna função de cleanup para parar o monitoramento
  return () => {
    if (timer) window.clearInterval(timer)
  }
}
