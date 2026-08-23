// Chamadas à API do módulo Venda (Fase 5/6 do backend).
import { api } from './client'

export function abrirVenda() {
  return api.post('/vendas')
}

export function listarVendas(status) {
  return api.get(status ? `/vendas?status=${status}` : '/vendas')
}

export function buscarVenda(id) {
  return api.get(`/vendas/${id}`)
}

export function adicionarItem(vendaId, dados) {
  return api.post(`/vendas/${vendaId}/itens`, dados)
}

export function atualizarQuantidade(vendaId, itemId, dados) {
  return api.put(`/vendas/${vendaId}/itens/${itemId}`, dados)
}

export function removerItem(vendaId, itemId) {
  return api.del(`/vendas/${vendaId}/itens/${itemId}`)
}

export function finalizarVenda(vendaId) {
  return api.post(`/vendas/${vendaId}/finalizar`)
}
