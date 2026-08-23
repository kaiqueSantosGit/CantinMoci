import { createContext, useContext, useEffect, useState } from 'react'
import { api, getToken, setToken, clearToken } from '../api/client'

const AuthContext = createContext(null)

/**
 * Provedor de autenticação — guarda quem está logado (usuario) e expõe
 * login()/logout() pro resto do app. Envolve toda a árvore de componentes
 * em main.jsx.
 */
export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null)
  // Enquanto carregando=true, ainda não sabemos se existe uma sessão
  // válida — evita "piscar" a tela de login antes de confirmar o token
  // salvo no localStorage.
  const [carregando, setCarregando] = useState(true)

  useEffect(() => {
    async function restaurarSessao() {
      if (!getToken()) {
        setCarregando(false)
        return
      }
      try {
        // Token salvo de uma visita anterior — confirma que ainda é válido
        // e recupera os dados do usuário (GET /auth/me, Fase 8).
        const perfil = await api.get('/auth/me')
        setUsuario(perfil)
      } catch {
        clearToken()
      } finally {
        setCarregando(false)
      }
    }
    restaurarSessao()
  }, [])

  async function login(email, senha) {
    const resposta = await api.post('/auth/login', { email, senha })
    setToken(resposta.token)
    const perfil = await api.get('/auth/me')
    setUsuario(perfil)
  }

  function logout() {
    clearToken()
    setUsuario(null)
  }

  return (
    <AuthContext.Provider value={{ usuario, carregando, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
