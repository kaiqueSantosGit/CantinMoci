import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { listarEventos } from '../api/eventos'
import EventoFormModal from '../components/EventoFormModal'

function StatusBadge({ status }) {
  const aberto = status === 'ABERTO'
  return (
    <span
      className="inline-flex items-center gap-1.5 h-[22px] px-2.5 rounded-full text-[11.5px] font-bold"
      style={{
        background: aberto ? 'var(--success-tint)' : 'var(--surface-sunken)',
        color: aberto ? 'var(--success)' : 'var(--ink-faint)',
      }}
    >
      {aberto ? 'Aberto' : 'Encerrado'}
    </span>
  )
}

export default function Eventos() {
  const [eventos, setEventos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [modalAberto, setModalAberto] = useState(false)

  async function carregar() {
    setCarregando(true)
    setErro('')
    try {
      const dados = await listarEventos()
      // Mais recentes primeiro.
      dados.sort((a, b) => new Date(b.dataAbertura) - new Date(a.dataAbertura))
      setEventos(dados)
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
  }

  useEffect(() => {
    carregar()
  }, [])

  function handleSalvo() {
    setModalAberto(false)
    carregar()
  }

  function formatarData(iso) {
    return new Date(iso).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit' })
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <span className="text-[13px]" style={{ color: 'var(--ink-faint)' }}>
          {carregando ? 'Carregando…' : `${eventos.length} evento${eventos.length === 1 ? '' : 's'}`}
        </span>
        <button
          onClick={() => setModalAberto(true)}
          className="h-9 px-3.5 rounded-lg text-[13px] font-bold"
          style={{ background: 'var(--brand-tint)', color: 'var(--brand-strong)', border: '1px solid var(--brand)' }}
        >
          + Novo evento
        </button>
      </div>

      {erro && (
        <p className="text-[13px] mb-3 px-1" style={{ color: 'var(--danger)' }}>
          {erro}
        </p>
      )}

      {!carregando && eventos.length === 0 && !erro ? (
        <div className="rounded-xl text-center py-10" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
          <p className="text-sm font-semibold mb-1">Nenhum evento cadastrado</p>
          <p className="text-[13px] m-0" style={{ color: 'var(--ink-faint)' }}>
            Clique em "+ Novo evento" pra abrir o primeiro.
          </p>
        </div>
      ) : (
        <>
          {/* Celular/tablet: lista de cartões */}
          <div className="md:hidden flex flex-col gap-2.5">
            {eventos.map((evento) => (
              <Link
                key={evento.id}
                to={`/eventos/${evento.id}`}
                className="block rounded-xl p-4"
                style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}
              >
                <div className="flex items-start justify-between gap-3">
                  <span className="text-[13.5px] font-semibold" style={{ color: 'var(--brand-strong)' }}>{evento.nome}</span>
                  <StatusBadge status={evento.status} />
                </div>
                <p className="text-[12.5px] m-0 mt-2" style={{ color: 'var(--ink-faint)' }}>
                  {evento.local || 'sem local informado'} · <span className="num">{formatarData(evento.dataAbertura)}</span>
                </p>
              </Link>
            ))}
          </div>

          {/* Desktop: tabela */}
          <div className="hidden md:block rounded-xl overflow-hidden" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
            <table className="w-full text-[13.5px]" style={{ tableLayout: 'fixed' }}>
              <colgroup>
                <col style={{ width: '38%' }} />
                <col style={{ width: '26%' }} />
                <col style={{ width: '18%' }} />
                <col style={{ width: '18%' }} />
              </colgroup>
              <thead>
                <tr>
                  <th className="text-left text-[11.5px] uppercase tracking-wide font-bold py-2.5 px-5" style={{ color: 'var(--ink-faint)', borderBottom: '1px solid var(--line)' }}>Evento</th>
                  <th className="text-left text-[11.5px] uppercase tracking-wide font-bold py-2.5 px-5" style={{ color: 'var(--ink-faint)', borderBottom: '1px solid var(--line)' }}>Local</th>
                  <th className="text-left text-[11.5px] uppercase tracking-wide font-bold py-2.5 px-5" style={{ color: 'var(--ink-faint)', borderBottom: '1px solid var(--line)' }}>Status</th>
                  <th className="text-left text-[11.5px] uppercase tracking-wide font-bold py-2.5 px-5" style={{ color: 'var(--ink-faint)', borderBottom: '1px solid var(--line)' }}>Aberto em</th>
                </tr>
              </thead>
              <tbody>
                {eventos.map((evento) => (
                  <tr key={evento.id}>
                    <td className="py-3 px-5" style={{ borderBottom: '1px solid var(--line)' }}>
                      <Link to={`/eventos/${evento.id}`} className="font-semibold" style={{ color: 'var(--brand-strong)' }}>
                        {evento.nome}
                      </Link>
                    </td>
                    <td className="py-3 px-5" style={{ borderBottom: '1px solid var(--line)', color: 'var(--ink-soft)' }}>{evento.local || '—'}</td>
                    <td className="py-3 px-5" style={{ borderBottom: '1px solid var(--line)' }}><StatusBadge status={evento.status} /></td>
                    <td className="py-3 px-5 num" style={{ borderBottom: '1px solid var(--line)', color: 'var(--ink-soft)' }}>{formatarData(evento.dataAbertura)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}

      {modalAberto && <EventoFormModal onFechar={() => setModalAberto(false)} onSalvo={handleSalvo} />}
    </div>
  )
}
