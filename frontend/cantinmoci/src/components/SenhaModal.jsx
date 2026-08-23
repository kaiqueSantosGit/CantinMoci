import { useState } from 'react'

/**
 * Modal genérico para "definir uma nova senha" — usado tanto por
 * "ADMIN reseta a senha de outro usuário" quanto por "trocar a própria
 * senha". A diferença entre os dois casos fica só em qual função de API
 * é chamada (passada via onSalvar), não na tela em si.
 */
export default function SenhaModal({ titulo, subtitulo, onFechar, onSalvar }) {
  const [novaSenha, setNovaSenha] = useState('')
  const [erro, setErro] = useState('')
  const [salvando, setSalvando] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()
    setErro('')

    if (novaSenha.length < 6) {
      setErro('A senha deve ter pelo menos 6 caracteres.')
      return
    }

    setSalvando(true)
    try {
      await onSalvar(novaSenha)
    } catch (e) {
      setErro(e.message)
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
        <h3 className="text-base font-semibold mb-1">{titulo}</h3>
        {subtitulo && <p className="text-[13px] mb-4 mt-0" style={{ color: 'var(--ink-faint)' }}>{subtitulo}</p>}

        <div className="mb-2">
          <label htmlFor="novaSenha" className="block text-[13px] font-semibold mb-1.5" style={{ color: 'var(--ink-soft)' }}>Nova senha</label>
          <input
            id="novaSenha"
            type="text"
            autoFocus
            value={novaSenha}
            onChange={(e) => setNovaSenha(e.target.value)}
            placeholder="mín. 6 caracteres"
            className="w-full h-[42px] rounded-[9px] px-3 text-sm outline-none"
            style={{ background: 'var(--paper)', color: 'var(--ink)', border: '1px solid var(--line)' }}
          />
        </div>

        {erro && <p className="text-[13px] mt-2 mb-0" style={{ color: 'var(--danger)' }}>{erro}</p>}

        <div className="flex gap-2 mt-5">
          <button type="button" onClick={onFechar} disabled={salvando} className="flex-1 h-[42px] rounded-[9px] text-sm font-semibold" style={{ background: 'var(--surface-sunken)', color: 'var(--ink-soft)' }}>
            Cancelar
          </button>
          <button type="submit" disabled={salvando} className="flex-1 h-[42px] rounded-[9px] text-sm font-bold disabled:opacity-60" style={{ background: 'var(--brand)', color: 'var(--surface)' }}>
            {salvando ? 'Salvando…' : 'Salvar'}
          </button>
        </div>
      </form>
    </div>
  )
}
