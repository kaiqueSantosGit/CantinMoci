import { useState } from 'react'
import { cadastrarUsuario } from '../api/usuarios'

/**
 * Cadastro de novo usuário — só criar (o backend não tem edição de nome/
 * email/cargo, só troca/reset de senha e desativação).
 */
export default function UsuarioFormModal({ onFechar, onSalvo }) {
  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [cargo, setCargo] = useState('OPERADOR')
  const [erro, setErro] = useState('')
  const [salvando, setSalvando] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()
    setErro('')

    if (!nome.trim() || !email.trim()) {
      setErro('Preencha nome e email.')
      return
    }
    if (senha.length < 6) {
      setErro('A senha deve ter pelo menos 6 caracteres.')
      return
    }

    setSalvando(true)
    try {
      await cadastrarUsuario({ nome: nome.trim(), email: email.trim(), senha, cargo })
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
        <h3 className="text-base font-semibold mb-4">Novo usuário</h3>

        <div className="mb-3.5">
          <label htmlFor="nome" className="block text-[13px] font-semibold mb-1.5" style={{ color: 'var(--ink-soft)' }}>Nome</label>
          <input
            id="nome"
            type="text"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
            placeholder="Maria Operadora"
            className="w-full h-[42px] rounded-[9px] px-3 text-sm outline-none"
            style={{ background: 'var(--paper)', color: 'var(--ink)', border: '1px solid var(--line)' }}
          />
        </div>

        <div className="mb-3.5">
          <label htmlFor="email" className="block text-[13px] font-semibold mb-1.5" style={{ color: 'var(--ink-soft)' }}>Email</label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="maria@cantinmoci.com"
            className="w-full h-[42px] rounded-[9px] px-3 text-sm outline-none"
            style={{ background: 'var(--paper)', color: 'var(--ink)', border: '1px solid var(--line)' }}
          />
        </div>

        <div className="grid grid-cols-2 gap-3 mb-2">
          <div>
            <label htmlFor="senha" className="block text-[13px] font-semibold mb-1.5" style={{ color: 'var(--ink-soft)' }}>Senha inicial</label>
            <input
              id="senha"
              type="text"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              placeholder="mín. 6 caracteres"
              className="w-full h-[42px] rounded-[9px] px-3 text-sm outline-none"
              style={{ background: 'var(--paper)', color: 'var(--ink)', border: '1px solid var(--line)' }}
            />
          </div>
          <div>
            <label htmlFor="cargo" className="block text-[13px] font-semibold mb-1.5" style={{ color: 'var(--ink-soft)' }}>Cargo</label>
            <select
              id="cargo"
              value={cargo}
              onChange={(e) => setCargo(e.target.value)}
              className="w-full h-[42px] rounded-[9px] px-2.5 text-sm outline-none"
              style={{ background: 'var(--paper)', color: 'var(--ink)', border: '1px solid var(--line)' }}
            >
              <option value="OPERADOR">Operador</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>
        </div>

        {erro && <p className="text-[13px] mt-2 mb-0" style={{ color: 'var(--danger)' }}>{erro}</p>}

        <div className="flex gap-2 mt-5">
          <button type="button" onClick={onFechar} disabled={salvando} className="flex-1 h-[42px] rounded-[9px] text-sm font-semibold" style={{ background: 'var(--surface-sunken)', color: 'var(--ink-soft)' }}>
            Cancelar
          </button>
          <button type="submit" disabled={salvando} className="flex-1 h-[42px] rounded-[9px] text-sm font-bold disabled:opacity-60" style={{ background: 'var(--brand)', color: 'var(--surface)' }}>
            {salvando ? 'Cadastrando…' : 'Cadastrar'}
          </button>
        </div>
      </form>
    </div>
  )
}
