import { useState } from 'react'
import { criarProduto, atualizarProduto } from '../api/produtos'

/**
 * Formulário de criar/editar produto, dentro de um modal.
 *
 * Um componente só cobre os dois casos: se "produto" vier preenchido
 * (veio de um clique em "editar"), o formulário nasce com os dados dele
 * e salva via PUT; se vier nulo (clique em "novo produto"), salva via POST.
 * Evita duplicar o formulário inteiro pra cada caso.
 */
export default function ProdutoFormModal({ produto, onFechar, onSalvo }) {
  const editando = Boolean(produto)
  const [nome, setNome] = useState(produto?.nome ?? '')
  const [preco, setPreco] = useState(produto?.preco ?? '')
  const [quantidadeEmEstoque, setQuantidadeEmEstoque] = useState(produto?.quantidadeEmEstoque ?? '')
  const [erro, setErro] = useState('')
  const [salvando, setSalvando] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()
    setErro('')

    if (!nome.trim()) {
      setErro('Informe o nome do produto.')
      return
    }
    if (preco === '' || Number(preco) < 0) {
      setErro('Informe um preço válido (0 ou maior).')
      return
    }
    if (quantidadeEmEstoque === '' || Number(quantidadeEmEstoque) < 0) {
      setErro('Informe uma quantidade em estoque válida (0 ou maior).')
      return
    }

    const dados = {
      nome: nome.trim(),
      preco: Number(preco),
      quantidadeEmEstoque: Number(quantidadeEmEstoque),
    }

    setSalvando(true)
    try {
      if (editando) {
        await atualizarProduto(produto.id, dados)
      } else {
        await criarProduto(dados)
      }
      onSalvo()
    } catch (e) {
      setErro(e.message)
    } finally {
      setSalvando(false)
    }
  }

  return (
    <div className="fixed inset-0 flex items-center justify-center p-4 z-50" style={{ background: 'rgba(0,0,0,0.45)' }}>
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-sm rounded-2xl p-6"
        style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}
      >
        <h3 className="text-base font-semibold mb-4">{editando ? 'Editar produto' : 'Novo produto'}</h3>

        <div className="mb-3.5">
          <label htmlFor="nome" className="block text-[13px] font-semibold mb-1.5" style={{ color: 'var(--ink-soft)' }}>
            Nome
          </label>
          <input
            id="nome"
            type="text"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
            placeholder="Coxinha"
            className="w-full h-[42px] rounded-[9px] px-3 text-sm outline-none"
            style={{ background: 'var(--paper)', color: 'var(--ink)', border: '1px solid var(--line)' }}
          />
        </div>

        <div className="grid grid-cols-2 gap-3 mb-2">
          <div>
            <label htmlFor="preco" className="block text-[13px] font-semibold mb-1.5" style={{ color: 'var(--ink-soft)' }}>
              Preço (R$)
            </label>
            <input
              id="preco"
              type="number"
              step="0.01"
              min="0"
              value={preco}
              onChange={(e) => setPreco(e.target.value)}
              placeholder="5.00"
              className="w-full h-[42px] rounded-[9px] px-3 text-sm outline-none num"
              style={{ background: 'var(--paper)', color: 'var(--ink)', border: '1px solid var(--line)' }}
            />
          </div>
          <div>
            <label htmlFor="estoque" className="block text-[13px] font-semibold mb-1.5" style={{ color: 'var(--ink-soft)' }}>
              Estoque
            </label>
            <input
              id="estoque"
              type="number"
              step="1"
              min="0"
              value={quantidadeEmEstoque}
              onChange={(e) => setQuantidadeEmEstoque(e.target.value)}
              placeholder="30"
              className="w-full h-[42px] rounded-[9px] px-3 text-sm outline-none num"
              style={{ background: 'var(--paper)', color: 'var(--ink)', border: '1px solid var(--line)' }}
            />
          </div>
        </div>

        {erro && (
          <p className="text-[13px] mt-2 mb-0" style={{ color: 'var(--danger)' }}>
            {erro}
          </p>
        )}

        <div className="flex gap-2 mt-5">
          <button
            type="button"
            onClick={onFechar}
            disabled={salvando}
            className="flex-1 h-[42px] rounded-[9px] text-sm font-semibold"
            style={{ background: 'var(--surface-sunken)', color: 'var(--ink-soft)' }}
          >
            Cancelar
          </button>
          <button
            type="submit"
            disabled={salvando}
            className="flex-1 h-[42px] rounded-[9px] text-sm font-bold disabled:opacity-60"
            style={{ background: 'var(--brand)', color: 'var(--surface)' }}
          >
            {salvando ? 'Salvando…' : 'Salvar'}
          </button>
        </div>
      </form>
    </div>
  )
}
