// Chamadas à API do módulo Evento (Fase 6 do backend).
import { api } from './client'

export function listarEventos() {
  return api.get('/eventos')
}

export function criarEvento(dados) {
  return api.post('/eventos', dados)
}

export function buscarEvento(id) {
  return api.get(`/eventos/${id}`)
}

export function encerrarEvento(id) {
  return api.post(`/eventos/${id}/encerrar`)
}

export function listarProdutosDoEvento(id) {
  return api.get(`/eventos/${id}/produtos`)
}

export function alocarEstoque(id, dados) {
  return api.post(`/eventos/${id}/produtos`, dados)
}

export function obterRelatorio(id) {
  return api.get(`/eventos/${id}/relatorio`)
}
