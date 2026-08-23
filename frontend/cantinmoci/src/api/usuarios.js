// Chamadas à API do módulo Gestão de Usuários (Fase 7 do backend).
import { api } from './client'

// ADMIN — gestão de outros usuários
export function listarUsuarios() {
  return api.get('/usuarios')
}

export function cadastrarUsuario(dados) {
  return api.post('/auth/register', dados)
}

export function desativarUsuario(id) {
  return api.del(`/usuarios/${id}`)
}

export function resetarSenha(id, dados) {
  return api.put(`/usuarios/${id}/senha`, dados)
}

// Qualquer usuário logado — a própria conta
export function trocarMinhaSenha(dados) {
  return api.put('/auth/me/senha', dados)
}
