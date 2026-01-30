const API_URL = import.meta.env.VITE_API_URL ?? ''

type RequestInitWithAuth = RequestInit & { auth?: boolean }

async function request(path: string, options: RequestInitWithAuth = {}) {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string> | undefined)
  }

  if ((options as RequestInitWithAuth).auth !== false) {
    const token = localStorage.getItem('token')
    if (token) headers['Authorization'] = `Bearer ${token}`
  }

  const { auth, ...fetchOpts } = options as any

  const res = await fetch(`${API_URL}${path}`, {
    ...fetchOpts,
    headers
  })

  if (!res.ok) {
    const text = await res.text().catch(() => '')
    const err = new Error(text || res.statusText)
    ;(err as any).status = res.status
    throw err
  }

  if (res.status === 204) return null
  return res.json()
}

export default {
  get: (path: string) => request(path, { method: 'GET' }),
  post: (path: string, body: any, opts: RequestInitWithAuth = {}) =>
    request(path, { method: 'POST', body: JSON.stringify(body), ...opts }),
  put: (path: string, body: any, opts: RequestInitWithAuth = {}) =>
    request(path, { method: 'PUT', body: JSON.stringify(body), ...opts }),
  del: (path: string) => request(path, { method: 'DELETE' })
}
