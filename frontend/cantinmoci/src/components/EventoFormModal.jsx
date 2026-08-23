import { useState } from 'react'
import { criarEvento } from '../api/eventos'

/**
 * Formulário de criar evento — só criar, não editar (o backend não tem
 * PUT /eventos/{id}; um evento só muda de estado ao ser encerrado).
 */
export default function EventoFormModal({ onFechar, onSalvo }) {
  const [nome, setNome] = useState('')
  const [local, setLocal] = useState('')
  const [erro, setErro] = useState('')
  const [salvando, setSalvando] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()
    setErro('')

    if (!nome.trim()) {
      setErro('Informe o nome do evento.')
      return
    }

    setSalvando(true)
    try {
      await criarEvento({ nome: nome.trim(), local: local.trim() || null })
      onSalvo()
    } catch (e) {
      // Mensagem mais comum aqui: já existe outro evento ABERTO (409) —
      // a mensagem do backend já explica isso, mostramos direto.
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
        <h3 className="text-base font-semibold mb-4">Novo evento</h3>

        <div className="mb-3.5">
          <label htmlFor="nome" className="block text-[13px] font-semibold mb-1.5" style={{ color: 'var(--ink-soft)' }}>
            Nome
          </label>
          <input
            id="nome"
            type="text"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
            placeholder="Festa Junina 2026"
            className="w-full h-[42px] rounded-[9px] px-3 text-sm outline-none"
            style={{ background: 'var(--paper)', color: 'var(--ink)', border: '1px solid var(--line)' }}
          />
        </div>

        <div className="mb-2">
          <label htmlFor="local" className="block text-[13px] font-semibold mb-1.5" style={{ color: 'var(--ink-soft)' }}>
            Local <span style={{ color: 'var(--ink-faint)', fontWeight: 500 }}>(opcional)</span>
          </label>
          <input
            id="local"
            type="text"
            value={local}
            onChange={(e) => setLocal(e.target.value)}
            placeholder="Salão Paroquial"
            className="w-full h-[42px] rounded-[9px] px-3 text-sm outline-none"
            style={{ background: 'var(--paper)', color: 'var(--ink)', border: '1px solid var(--line)' }}
          />
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
            {salvando ? 'Criando…' : 'Criar evento'}
          </button>
        </div>
      </form>
    </div>
  )
}
