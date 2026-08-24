import { useState } from 'react'
import { NavLink, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { trocarMinhaSenha } from '../api/usuarios'
import SenhaModal from '../components/SenhaModal'

const TITULOS_POR_ROTA = {
  '/': 'Painel',
  '/vendas': 'Vendas',
  '/eventos': 'Eventos',
  '/produtos': 'Produtos',
  '/usuarios': 'Usuários',
}

const ITENS_NAV = [
  {
    to: '/',
    label: 'Painel',
    end: true,
    icone: (
      <>
        <rect x="3" y="3" width="7" height="9" rx="1.5" />
        <rect x="14" y="3" width="7" height="5" rx="1.5" />
        <rect x="14" y="12" width="7" height="9" rx="1.5" />
        <rect x="3" y="16" width="7" height="5" rx="1.5" />
      </>
    ),
  },
  {
    to: '/vendas',
    label: 'Vendas',
    icone: (
      <>
        <circle cx="9" cy="20" r="1.4" />
        <circle cx="17" cy="20" r="1.4" />
        <path d="M3 4h2l2.2 11.2a2 2 0 0 0 2 1.8h7.6a2 2 0 0 0 2-1.6L21 8H6" />
      </>
    ),
  },
  {
    to: '/eventos',
    label: 'Eventos',
    icone: (
      <>
        <rect x="3" y="5" width="18" height="16" rx="2" />
        <path d="M3 10h18M8 3v4M16 3v4" />
      </>
    ),
  },
  {
    to: '/produtos',
    label: 'Produtos',
    icone: (
      <>
        <path d="M21 8l-9-5-9 5 9 5 9-5z" />
        <path d="M3 8v8l9 5 9-5V8M12 13v8" />
      </>
    ),
  },
  {
    to: '/usuarios',
    label: 'Usuários',
    somenteAdmin: true,
    icone: (
      <>
        <circle cx="9" cy="8" r="3.4" />
        <path d="M2.5 20c0-3.6 2.9-6.4 6.5-6.4s6.5 2.8 6.5 6.4" />
        <path d="M16 4.3a3.4 3.4 0 0 1 0 6.6M21.5 20a5.8 5.8 0 0 0-4.5-6" />
      </>
    ),
  },
]

function Icone({ children, className = 'w-[17px] h-[17px]' }) {
  return (
    <svg viewBox="0 0 24 24" fill="none" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={`${className} shrink-0`} style={{ stroke: 'currentColor' }}>
      {children}
    </svg>
  )
}

function LogoCantinMoci() {
  return (
    <div className="flex items-center gap-[9px] px-1.5">
      <span className="w-[30px] h-[30px] rounded-lg flex items-center justify-center shrink-0" style={{ background: 'var(--brand)' }}>
        <svg viewBox="0 0 24 24" fill="none" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="w-4 h-4" style={{ stroke: 'var(--surface)' }}>
          <path d="M4 8l2-4h12l2 4" />
          <path d="M4 8h16v11a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V8z" />
          <path d="M9 12v4M15 12v4" />
        </svg>
      </span>
      <strong className="text-[15px] font-extrabold">CantinMoci</strong>
    </div>
  )
}

/**
 * Conteúdo do menu (links + rodapé com usuário/ações) — compartilhado entre
 * a barra lateral fixa do desktop e a gaveta deslizante do mobile, pra não
 * duplicar a lista de links em dois lugares.
 */
function ConteudoMenu({ usuario, onNavegar, onTrocarSenha, onSair }) {
  return (
    <>
      <LogoCantinMoci />

      <nav className="flex flex-col gap-0.5">
        {ITENS_NAV.filter((item) => !item.somenteAdmin || usuario?.cargo === 'ADMIN').map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            onClick={onNavegar}
            className={({ isActive }) =>
              `flex items-center gap-2.5 h-[38px] px-2.5 rounded-lg text-[13.5px] font-semibold transition-colors ${
                isActive ? '' : 'hover:opacity-80'
              }`
            }
            style={({ isActive }) => ({
              background: isActive ? 'var(--brand-tint)' : 'transparent',
              color: isActive ? 'var(--brand-strong)' : 'var(--ink-soft)',
            })}
          >
            <Icone>{item.icone}</Icone>
            {item.label}
          </NavLink>
        ))}
      </nav>

      <div className="mt-auto pt-[14px]" style={{ borderTop: '1px solid var(--line)' }}>
        <div className="flex items-center gap-2.5">
          <span
            className="w-[30px] h-[30px] rounded-full flex items-center justify-center text-xs font-bold shrink-0"
            style={{ background: 'var(--accent-tint)', color: 'var(--accent)' }}
          >
            {usuario?.nome
              ?.split(' ')
              .map((p) => p[0])
              .slice(0, 2)
              .join('')
              .toUpperCase()}
          </span>
          <span className="min-w-0">
            <strong className="block text-[12.5px] truncate">{usuario?.nome}</strong>
            <span className="block text-[11px]" style={{ color: 'var(--ink-faint)' }}>{usuario?.cargo}</span>
          </span>
        </div>
        <div className="flex items-center gap-3 mt-2.5 pl-[2px]">
          <button onClick={onTrocarSenha} className="text-[11px] font-semibold" style={{ color: 'var(--ink-faint)' }}>
            Trocar senha
          </button>
          <button onClick={onSair} className="text-[11px] font-semibold" style={{ color: 'var(--ink-faint)' }}>
            Sair
          </button>
        </div>
      </div>
    </>
  )
}

export default function AppShell() {
  const { usuario, logout } = useAuth()
  const location = useLocation()
  const [modalSenha, setModalSenha] = useState(false)
  const [menuAberto, setMenuAberto] = useState(false)
  const titulo =
    TITULOS_POR_ROTA[location.pathname] ??
    (location.pathname.startsWith('/eventos/') ? 'Eventos' : 'CantinMoci')

  async function handleTrocarSenha(novaSenha) {
    await trocarMinhaSenha({ novaSenha })
    setModalSenha(false)
  }

  function abrirModalSenha() {
    setMenuAberto(false)
    setModalSenha(true)
  }

  return (
    <div className="min-h-screen flex flex-col md:flex-row">
      {/* Barra superior — só no mobile/tablet (menu vira gaveta) */}
      <div
        className="md:hidden flex items-center justify-between h-14 px-4 shrink-0"
        style={{ background: 'var(--surface)', borderBottom: '1px solid var(--line)' }}
      >
        <button
          onClick={() => setMenuAberto(true)}
          aria-label="Abrir menu"
          className="w-9 h-9 -ml-1.5 flex items-center justify-center rounded-lg"
        >
          <Icone className="w-5 h-5"><path d="M4 7h16M4 12h16M4 17h16" /></Icone>
        </button>
        <h2 className="text-[15px] font-semibold">{titulo}</h2>
        <span
          className="w-8 h-8 rounded-full flex items-center justify-center text-[11px] font-bold shrink-0"
          style={{ background: 'var(--accent-tint)', color: 'var(--accent)' }}
        >
          {usuario?.nome
            ?.split(' ')
            .map((p) => p[0])
            .slice(0, 2)
            .join('')
            .toUpperCase()}
        </span>
      </div>

      {/* Gaveta do menu mobile — só existe (e só recebe clique) quando aberta */}
      {menuAberto && (
        <div className="md:hidden fixed inset-0 z-50 flex">
          <div className="absolute inset-0" style={{ background: 'rgba(0,0,0,0.45)' }} onClick={() => setMenuAberto(false)} />
          <aside
            className="relative w-[248px] max-w-[80vw] h-full flex flex-col gap-7 px-[14px] py-5 overflow-y-auto"
            style={{ background: 'var(--surface)', borderRight: '1px solid var(--line)' }}
          >
            <ConteudoMenu usuario={usuario} onNavegar={() => setMenuAberto(false)} onTrocarSenha={abrirModalSenha} onSair={logout} />
          </aside>
        </div>
      )}

      {/* Barra lateral fixa — só no desktop */}
      <aside
        className="hidden md:flex w-[216px] shrink-0 flex-col gap-7 px-[14px] py-5"
        style={{ background: 'var(--surface)', borderRight: '1px solid var(--line)' }}
      >
        <ConteudoMenu usuario={usuario} onTrocarSenha={() => setModalSenha(true)} onSair={logout} />
      </aside>

      {modalSenha && (
        <SenhaModal
          titulo="Trocar minha senha"
          onFechar={() => setModalSenha(false)}
          onSalvar={handleTrocarSenha}
        />
      )}

      <div className="flex-1 min-w-0 flex flex-col">
        <div className="hidden md:flex h-[60px] shrink-0 items-center justify-between px-6" style={{ background: 'var(--surface)', borderBottom: '1px solid var(--line)' }}>
          <h2 className="text-base font-semibold">{titulo}</h2>
        </div>
        <div className="p-4 md:p-6 overflow-x-auto">
          <Outlet />
        </div>
      </div>
    </div>
  )
}
