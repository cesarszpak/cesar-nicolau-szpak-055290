import axios from 'axios'
import type { AxiosRequestConfig } from 'axios'

/**
 * URL base da API obtida a partir das variáveis de ambiente do Vite.
 * Caso não esteja definida, utiliza o host atual na porta 8080
 * (útil em ambiente de desenvolvimento ou Docker).
 */
const API_URL =
  import.meta.env.VITE_API_URL ??
  (typeof window !== 'undefined'
    ? `${window.location.protocol}//${window.location.hostname}:8080`
    : '')

/**
 * Opções customizadas para a requisição HTTP.
 * authRequired?: define se o token JWT deve ser enviado (padrão: true).
 */
type RequestOptions = AxiosRequestConfig & { authRequired?: boolean }

/**
 * Instância do Axios configurada com a URL base da API.
 */
const instance = axios.create({ baseURL: API_URL })

/**
 * Configuração interna do Axios que permite
 * o uso de flags adicionais (_retry, authRequired).
 */
interface InternalAxiosRequestConfig extends RequestOptions {
  _retry?: boolean
}

/**
 * Promise compartilhada para evitar múltiplas chamadas
 * simultâneas de refresh de token.
 */
let refreshPromise: Promise<string | null> | null = null

/**
 * Interceptor global de respostas.
 *
 * - Em respostas bem-sucedidas, marca o backend como UP.
 * - Em erros 401, executa o refresh do token e refaz a requisição original.
 * - Em erros de rede ou 503, marca o backend como DOWN.
 */
instance.interceptors.response.use(
  response => {
    // Em respostas de sucesso, marca o backend como UP
    // (import dinâmico evita incluir código desnecessário no bundle)
    import('./backendMonitor')
      .then(bm => {
        if (bm && bm.setUp) bm.setUp()
      })
      .catch(() => {})
    return response
  },
  async error => {
    const { response, config } = error
    const originalConfig = config as InternalAxiosRequestConfig | undefined

    // Monitoramento proativo:
    // se não houver resposta ou o status for 503, marca o backend como DOWN
    if (!response || response.status === 503) {
      import('./backendMonitor')
        .then(bm => {
          if (bm && bm.setDown) {
            bm.setDown(
              response
                ? response.statusText || String(response.status)
                : error.message || 'network'
            )
          }
        })
        .catch(() => {})
    }

    // Se não houver resposta, não for 401,
    // ou a rota não exigir autenticação, apenas propaga o erro
    if (
      !response ||
      response.status !== 401 ||
      (originalConfig && originalConfig.authRequired === false)
    ) {
      return Promise.reject(error)
    }

    // Evita loop infinito de tentativas de refresh
    if (originalConfig && originalConfig._retry) {
      return Promise.reject(error)
    }

    // Inicia o fluxo de refresh apenas uma vez
    if (!refreshPromise) {
      const refreshToken = localStorage.getItem('refreshToken')

      // Se não houver refresh token, limpa a sessão
      if (!refreshToken) {
        localStorage.removeItem('token')
        localStorage.removeItem('refreshToken')
        return Promise.reject(error)
      }

      // Inicia o processo de refresh do token
      // Usa authRequired: false para evitar interceptação em loop
      refreshPromise = instance
        .post(
          '/refresh',
          refreshToken,
          ({ headers: { 'Content-Type': 'text/plain' }, authRequired: false } as any)
        )
        .then(r => {
          const data = r.data

          // Atualiza os tokens no localStorage
          if (data && data.token) {
            localStorage.setItem('token', data.token)
            if (data.refreshToken) {
              localStorage.setItem('refreshToken', data.refreshToken)
            }
            return data.token
          }

          // Falha no refresh
          localStorage.removeItem('token')
          localStorage.removeItem('refreshToken')
          throw new Error('Falha ao renovar o token')
        })
        .catch(e => {
          // Em caso de erro, limpa os tokens
          localStorage.removeItem('token')
          localStorage.removeItem('refreshToken')
          throw e
        })
        .finally(() => {
          refreshPromise = null
        })
    }

    try {
      // Aguarda o novo token e refaz a requisição original
      const newToken = await refreshPromise
      if (!originalConfig) return Promise.reject(error)

      originalConfig._retry = true
      if (!originalConfig.headers) originalConfig.headers = {} as any

      ;(originalConfig.headers as any)['Authorization'] = `Bearer ${newToken}`
      return instance.request(originalConfig)
    } catch (e) {
      return Promise.reject(e)
    }
  }
)

/**
 * Função central de requisição HTTP.
 * Centraliza autenticação, refresh de token e tratamento de erros.
 */
async function request(path: string, options: RequestOptions = {}) {
  // Extrai a flag de autenticação e headers customizados
  const { authRequired = true, headers: optHeaders, ...cfg } = options

  // Monta os headers padrão da requisição
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(optHeaders as Record<string, string> | undefined)
  }

  // Se o body for FormData, permite que o navegador defina o Content-Type (boundary)
  if ((cfg as any).data instanceof FormData) {
    delete headers['Content-Type']
  }

  // Adiciona o token JWT no header Authorization, se necessário
  if (authRequired) {
    const token = localStorage.getItem('token')
    if (token) headers['Authorization'] = `Bearer ${token}`
  }

  try {
    // Executa a requisição HTTP
    const res = await instance.request({ url: path, headers, ...cfg })

    // Retorna null se não houver conteúdo
    if (res.status === 204) return null

    return res.data
  } catch (err: any) {
    const status = err?.response?.status
    const data = err?.response?.data

    // Erro sem resposta: problema de rede/CORS ou o servidor encerrou a conexão
    if (!err?.response) {
      const msg = (err.message || '').toLowerCase().includes('network')
        // ? 'Erro de rede: verifique se o backend está acessível ou se há problemas de CORS'
        ? 'Erro de rede: aguardando resposta do backend'
        : err.message || 'Falha na requisição'
      const e = new Error(msg)
      ;(e as any).status = 0
      throw e
    }

    // Mensagem amigável para erro 403 (proibido)
    if (status === 403) {
      const e = new Error('Ação não autorizada. Faça login.')
      ;(e as any).status = status
      throw e
    }

    // Monta a mensagem de erro a partir da resposta da API
    let text = err.message || 'Falha na requisição'
    if (data) {
      if (typeof data === 'string') text = data
      else if ((data as any).message) text = (data as any).message
      else text = JSON.stringify(data)
    }

    const e = new Error(text || 'Falha na requisição')
    ;(e as any).status = status || 0
    throw e
  }
}

/**
 * API pública utilizada pelo front-end.
 * Centraliza os métodos GET, POST, PUT e DELETE.
 */
export default {
  get: (path: string) =>
    request(path, { method: 'GET' }),

  post: (path: string, body: any, opts: RequestOptions = {}) =>
    // Se o body for string (ex: refresh token), não serializa como JSON
    request(path, {
      method: 'POST',
      data: typeof body === 'string' ? body : body,
      headers:
        typeof body === 'string'
          ? { 'Content-Type': 'text/plain' }
          : undefined,
      ...opts
    }),

  put: (path: string, body: any, opts: RequestOptions = {}) =>
    request(path, { method: 'PUT', data: body, ...opts }),

  del: (path: string) =>
    request(path, { method: 'DELETE' })
}
