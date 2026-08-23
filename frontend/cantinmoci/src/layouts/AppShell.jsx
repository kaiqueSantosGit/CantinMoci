import { NavLink, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

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

function Icone({ children }) {
  return (
    <svg viewBox="0 0 24 24" fill="none" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="w-[17px] h-[17px] shrink-0" style={{ stroke: 'currentColor' }}>
      {children}
    </svg>
  )
}

export default function AppShell() {
  const { usuario, logout } = useAuth()
  const location = useLocation()
  const titulo =
    TITULOS_POR_ROTA[location.pathname] ??
    (location.pathname.startsWith('/eventos/') ? 'Eventos' : 'CantinMoci')
  const iniciais = usuario?.nome
    ?.split(' ')
    .map((p) => p[0])
    .slice(0, 2)
    .join('')
    .toUpperCase()

  return (
    <div className="min-h-screen flex">
      <aside
        className="w-[216px] shrink-0 flex flex-col gap-7 px-[14px] py-5"
        style={{ background: 'var(--surface)', borderRight: '1px solid var(--line)' }}
      >
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

        <nav className="flex flex-col gap-0.5">
          {ITENS_NAV.filter((item) => !item.somenteAdmin || usuario?.cargo === 'ADMIN').map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
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

        <div className="mt-auto flex items-center gap-2.5 pt-[14px]" style={{ borderTop: '1px solid var(--line)' }}>
          <span
            className="w-[30px] h-[30px] rounded-full flex items-center justify-center text-xs font-bold shrink-0"
            style={{ background: 'var(--accent-tint)', color: 'var(--accent)' }}
          >
            {iniciais}
          </span>
          <span className="min-w-0">
            <strong className="block text-[12.5px] truncate">{usuario?.nome}</strong>
            <span className="block text-[11px]" style={{ color: 'var(--ink-faint)' }}>{usuario?.cargo}</span>
          </span>
          <button
            onClick={logout}
            className="ml-auto text-[11px] font-semibold shrink-0"
            style={{ color: 'var(--ink-faint)' }}
          >
            Sair
          </button>
        </div>
      </aside>

      <div className="flex-1 min-w-0 flex flex-col">
        <div className="h-[60px] shrink-0 flex items-center justify-between px-6" style={{ background: 'var(--surface)', borderBottom: '1px solid var(--line)' }}>
          <h2 className="text-base font-semibold">{titulo}</h2>
        </div>
        <div className="p-6 overflow-x-auto">
          <Outlet />
        </div>
      </div>
    </div>
  )
}
