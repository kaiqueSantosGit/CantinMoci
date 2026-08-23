// Cliente HTTP central — todas as chamadas à API do CantinMoci passam por
// aqui. Centralizar num lugar só evita repetir "monta a URL, anexa o
// token, trata erro" em cada tela.

// VITE_API_URL vem de uma variável de ambiente (arquivo .env.development
// localmente; configurada no painel do Vercel/Netlify em produção).
// Fallback pro backend local, pra funcionar sem configurar nada de cara.
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const TOKEN_KEY = 'cantinmoci_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

/**
 * Faz uma requisição à API, anexando o token JWT automaticamente quando
 * existir. Lança um Error com a mensagem que o backend devolveu (o mesmo
 * campo "message" que aparece nas exceções customizadas do backend, tipo
 * EstoqueInsuficienteException, OperacaoInvalidaException etc.) — assim as
 * telas conseguem mostrar o motivo real do erro pro operador, não um erro
 * genérico.
 */
export async function apiFetch(path, options = {}) {
  const token = getToken()
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  }
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const response = await fetch(`${API_URL}${path}`, { ...options, headers })

  // 204 No Content (ex: DELETE, trocar senha) — não tem corpo pra ler.
  if (response.status === 204) {
    return null
  }

  const isJson = response.headers.get('content-type')?.includes('application/json')
  const body = isJson ? await response.json() : null

  if (!response.ok) {
    // Token inválido ou expirado (24h) — não adianta mostrar erro na tela
    // atual, o usuário precisa logar de novo.
    if (response.status === 401) {
      clearToken()
      window.location.href = '/login'
    }
    throw new Error(body?.message || 'Ocorreu um erro inesperado. Tente novamente.')
  }

  return body
}

export const api = {
  get: (path) => apiFetch(path),
  post: (path, data) => apiFetch(path, { method: 'POST', body: data ? JSON.stringify(data) : undefined }),
  put: (path, data) => apiFetch(path, { method: 'PUT', body: data ? JSON.stringify(data) : undefined }),
  del: (path) => apiFetch(path, { method: 'DELETE' }),
}
