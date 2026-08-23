import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

/**
 * Envolve rotas que exigem login. Enquanto ainda não sabemos se existe uma
 * sessão válida (carregando=true), não mostra nada — evita um "flash" da
 * tela de login antes de confirmar um token salvo no localStorage.
 */
export default function ProtectedRoute({ children }) {
  const { usuario, carregando } = useAuth()

  if (carregando) {
    return null
  }

  if (!usuario) {
    return <Navigate to="/login" replace />
  }

  return children
}
