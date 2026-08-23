// Chamadas à API do módulo Produto (Fase 2 do backend) — CRUD simples,
// sem novidade nenhuma no backend, só consumindo o que já existe.
import { api } from './client'

export function listarProdutos() {
  return api.get('/produtos')
}

export function criarProduto(dados) {
  return api.post('/produtos', dados)
}

export function atualizarProduto(id, dados) {
  return api.put(`/produtos/${id}`, dados)
}

export function desativarProduto(id) {
  return api.del(`/produtos/${id}`)
}
