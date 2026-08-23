import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()
    setErro('')

    if (!email.trim() || !senha.trim()) {
      setErro('Preencha email e senha.')
      return
    }

    setEnviando(true)
    try {
      await login(email.trim(), senha)
      navigate('/')
    } catch {
      // O AuthService usa a mesma mensagem genérica pra email inexistente e
      // senha errada (decisão de segurança da Fase 3) — reproduzimos isso
      // aqui em vez de mostrar o texto que o backend devolveu, pra não
      // criar uma segunda fonte de verdade sobre a mensagem.
      setErro('Email ou senha inválidos.')
    } finally {
      setEnviando(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-[360px] rounded-2xl p-9 py-9"
        style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}
      >
        <div className="flex items-center gap-2.5 mb-7">
          <span
            className="w-9 h-9 rounded-[10px] flex items-center justify-center shrink-0"
            style={{ background: 'var(--brand)' }}
          >
            <svg viewBox="0 0 24 24" fill="none" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="w-5 h-5" style={{ stroke: 'var(--surface)' }}>
              <path d="M4 8l2-4h12l2 4" />
              <path d="M4 8h16v11a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V8z" />
              <path d="M9 12v4M15 12v4" />
            </svg>
          </span>
          <span>
            <strong className="block text-[18px] font-extrabold tracking-tight">CantinMoci</strong>
            <span className="block text-xs" style={{ color: 'var(--ink-faint)' }}>gestão de cantinas beneficentes</span>
          </span>
        </div>

        <div className="mb-4">
          <label htmlFor="email" className="block text-[13px] font-semibold mb-1.5" style={{ color: 'var(--ink-soft)' }}>
            Email
          </label>
          <input
            id="email"
            type="email"
            autoComplete="username"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full h-[42px] rounded-[9px] px-3 text-sm outline-none focus:ring-2"
            style={{ background: 'var(--paper)', color: 'var(--ink)', border: '1px solid var(--line)' }}
          />
        </div>

        <div className="mb-2">
          <label htmlFor="senha" className="block text-[13px] font-semibold mb-1.5" style={{ color: 'var(--ink-soft)' }}>
            Senha
          </label>
          <input
            id="senha"
            type="password"
            autoComplete="current-password"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            className="w-full h-[42px] rounded-[9px] px-3 text-sm outline-none focus:ring-2"
            style={{ background: 'var(--paper)', color: 'var(--ink)', border: '1px solid var(--line)' }}
          />
        </div>

        {erro && (
          <p className="text-[13px] mt-2 mb-0" style={{ color: 'var(--danger)' }}>
            {erro}
          </p>
        )}

        <button
          type="submit"
          disabled={enviando}
          className="w-full h-11 rounded-[9px] mt-4 text-sm font-bold disabled:opacity-60"
          style={{ background: 'var(--brand)', color: 'var(--surface)' }}
        >
          {enviando ? 'Entrando…' : 'Entrar'}
        </button>
      </form>
    </div>
  )
}
