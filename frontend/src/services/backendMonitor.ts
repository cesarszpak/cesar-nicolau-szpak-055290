/**
 * Tipo de listener utilizado para notificar mudanças
 * no estado do backend (UP ou DOWN).
 */
type Listener = (status: 'UP' | 'DOWN', reason?: string) => void

/**
 * Conjunto de listeners inscritos para receber
 * atualizações de status do backend.
 */
const listeners = new Set<Listener>()

/**
 * Estado atual do backend.
 * Por padrão, assume que o serviço está UP.
 */
let current: { status: 'UP' | 'DOWN'; reason?: string } = { status: 'UP' }

/**
 * Inscreve um listener para receber atualizações de status.
 * O estado atual é emitido imediatamente após a inscrição.
 *
 * @param fn Função chamada sempre que o status mudar
 * @returns Função para cancelar a inscrição
 */
export function subscribe(fn: Listener) {
  listeners.add(fn)

  // Emite o estado atual imediatamente
  fn(current.status, current.reason)

  return () => listeners.delete(fn)
}

/**
 * Emite um novo status para todos os listeners inscritos
 * e atualiza o estado atual.
 *
 * @param status Novo status do backend (UP ou DOWN)
 * @param reason Motivo opcional da indisponibilidade
 */
function emit(status: 'UP' | 'DOWN', reason?: string) {
  current = { status, reason }
  for (const l of listeners) l(status, reason)
}

/**
 * Marca o backend como indisponível (DOWN).
 *
 * @param reason Motivo opcional da indisponibilidade
 */
export function setDown(reason?: string) {
  emit('DOWN', reason)
}

/**
 * Marca o backend como disponível (UP).
 */
export function setUp() {
  emit('UP')
}

/**
 * Retorna o estado atual do backend.
 */
export function getStatus() {
  return current
}
