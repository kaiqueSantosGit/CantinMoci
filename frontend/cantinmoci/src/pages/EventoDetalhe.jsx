import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { buscarEvento, listarProdutosDoEvento, alocarEstoque, encerrarEvento, obterRelatorio } from '../api/eventos'
import { listarProdutos } from '../api/produtos'

function moeda(valor) {
  return 'R$ ' + Number(valor).toFixed(2).replace('.', ',')
}

export default function EventoDetalhe() {
  const { id } = useParams()
  const navigate = useNavigate()

  const [evento, setEvento] = useState(null)
  const [estoque, setEstoque] = useState([])
  const [relatorio, setRelatorio] = useState(null)
  const [catalogo, setCatalogo] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  const [produtoSelecionado, setProdutoSelecionado] = useState('')
  const [quantidadeAlocar, setQuantidadeAlocar] = useState('')
  const [alocando, setAlocando] = useState(false)
  const [erroAlocar, setErroAlocar] = useState('')

  async function carregar() {
    setCarregando(true)
    setErro('')
    try {
      const [dadosEvento, dadosEstoque, dadosRelatorio, dadosCatalogo] = await Promise.all([
        buscarEvento(id),
        listarProdutosDoEvento(id),
        obterRelatorio(id),
        listarProdutos(),
      ])
      setEvento(dadosEvento)
      setEstoque(dadosEstoque)
      setRelatorio(dadosRelatorio)
      setCatalogo(dadosCatalogo)
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
  }

  useEffect(() => {
    carregar()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id])

  async function handleAlocar(event) {
    event.preventDefault()
    setErroAlocar('')

    if (!produtoSelecionado) {
      setErroAlocar('Escolha um produto.')
      return
    }
    if (quantidadeAlocar === '' || Number(quantidadeAlocar) < 1) {
      setErroAlocar('Informe uma quantidade válida (mínimo 1).')
      return
    }

    setAlocando(true)
    try {
      await alocarEstoque(id, { produtoId: Number(produtoSelecionado), quantidade: Number(quantidadeAlocar) })
      setProdutoSelecionado('')
      setQuantidadeAlocar('')
      carregar()
    } catch (e) {
      setErroAlocar(e.message)
    } finally {
      setAlocando(false)
    }
  }

  async function handleEncerrar() {
    const confirmou = window.confirm('Encerrar este evento? Não aceita mais vendas nem alocação de estoque depois disso.')
    if (!confirmou) return

    try {
      await encerrarEvento(id)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  if (carregando) {
    return <p className="text-sm" style={{ color: 'var(--ink-faint)' }}>Carregando…</p>
  }

  if (erro && !evento) {
    return <p className="text-sm" style={{ color: 'var(--danger)' }}>{erro}</p>
  }

  const aberto = evento.status === 'ABERTO'

  return (
    <div>
      <Link to="/eventos" className="text-[13px] font-semibold inline-block mb-3" style={{ color: 'var(--ink-faint)' }}>
        ← Voltar pra eventos
      </Link>

      <div className="rounded-xl p-5 mb-5 flex items-center justify-between" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
        <div>
          <h3 className="text-base font-semibold mb-1">{evento.nome}</h3>
          <p className="text-[13px] m-0" style={{ color: 'var(--ink-faint)' }}>
            {evento.local && <>{evento.local} · </>}
            <span
              className="font-bold"
              style={{ color: aberto ? 'var(--success)' : 'var(--ink-faint)' }}
            >
              {aberto ? 'Aberto' : 'Encerrado'}
            </span>
          </p>
        </div>
        {aberto && (
          <button
            onClick={handleEncerrar}
            className="h-9 px-3.5 rounded-lg text-[13px] font-bold shrink-0"
            style={{ background: 'var(--danger-tint)', color: 'var(--danger)' }}
          >
            Encerrar evento
          </button>
        )}
      </div>

      {erro && (
        <p className="text-[13px] mb-3 px-1" style={{ color: 'var(--danger)' }}>{erro}</p>
      )}

      <div className="grid gap-5" style={{ gridTemplateColumns: 'minmax(0, 1fr) minmax(0, 1fr)' }}>
        <div className="rounded-xl overflow-hidden" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
          <div className="px-5 py-3.5" style={{ borderBottom: '1px solid var(--line)' }}>
            <h4 className="text-[14.5px] font-semibold m-0">Estoque alocado</h4>
          </div>

          {estoque.length === 0 ? (
            <p className="text-[13px] text-center py-6 m-0" style={{ color: 'var(--ink-faint)' }}>
              Nenhum produto alocado ainda.
            </p>
          ) : (
            <table className="w-full text-[13px]">
              <tbody>
                {estoque.map((item) => (
                  <tr key={item.produtoId}>
                    <td className="py-2.5 px-5" style={{ borderBottom: '1px solid var(--line)' }}>{item.nomeProduto}</td>
                    <td className="py-2.5 px-5 num text-right" style={{ borderBottom: '1px solid var(--line)', color: 'var(--ink-faint)' }}>
                      {item.quantidadeAtual} / {item.quantidadeInicial}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          {aberto && (
            <form onSubmit={handleAlocar} className="p-5 pt-4 flex flex-col gap-2.5" style={{ borderTop: estoque.length > 0 ? '1px solid var(--line)' : 'none' }}>
              <div className="flex gap-2">
                <select
                  value={produtoSelecionado}
                  onChange={(e) => setProdutoSelecionado(e.target.value)}
                  className="flex-1 h-[38px] rounded-[9px] px-2.5 text-[13px] outline-none"
                  style={{ background: 'var(--paper)', color: 'var(--ink)', border: '1px solid var(--line)' }}
                >
                  <option value="">Produto…</option>
                  {catalogo.map((p) => (
                    <option key={p.id} value={p.id}>{p.nome}</option>
                  ))}
                </select>
                <input
                  type="number"
                  min="1"
                  step="1"
                  value={quantidadeAlocar}
                  onChange={(e) => setQuantidadeAlocar(e.target.value)}
                  placeholder="Qtd"
                  className="w-20 h-[38px] rounded-[9px] px-2.5 text-[13px] outline-none num"
                  style={{ background: 'var(--paper)', color: 'var(--ink)', border: '1px solid var(--line)' }}
                />
                <button
                  type="submit"
                  disabled={alocando}
                  className="h-[38px] px-3 rounded-[9px] text-[13px] font-bold shrink-0 disabled:opacity-60"
                  style={{ background: 'var(--brand)', color: 'var(--surface)' }}
                >
                  {alocando ? '…' : 'Alocar'}
                </button>
              </div>
              {erroAlocar && <p className="text-[12.5px] m-0" style={{ color: 'var(--danger)' }}>{erroAlocar}</p>}
            </form>
          )}
        </div>

        <div className="rounded-xl overflow-hidden" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
          <div className="px-5 py-3.5" style={{ borderBottom: '1px solid var(--line)' }}>
            <h4 className="text-[14.5px] font-semibold m-0">Relatório</h4>
          </div>

          <div className="grid grid-cols-3 gap-2.5 p-5">
            <div className="rounded-lg p-3" style={{ background: 'var(--surface-sunken)' }}>
              <div className="text-[11px] font-semibold" style={{ color: 'var(--ink-faint)' }}>Arrecadado</div>
              <div className="text-base font-semibold num mt-1">{moeda(relatorio.valorTotalArrecadado)}</div>
            </div>
            <div className="rounded-lg p-3" style={{ background: 'var(--surface-sunken)' }}>
              <div className="text-[11px] font-semibold" style={{ color: 'var(--ink-faint)' }}>Vendas</div>
              <div className="text-base font-semibold num mt-1">{relatorio.quantidadeVendas}</div>
            </div>
            <div className="rounded-lg p-3" style={{ background: 'var(--surface-sunken)' }}>
              <div className="text-[11px] font-semibold" style={{ color: 'var(--ink-faint)' }}>Ticket médio</div>
              <div className="text-base font-semibold num mt-1">{moeda(relatorio.ticketMedio)}</div>
            </div>
          </div>

          {relatorio.produtosMaisVendidos.length > 0 && (
            <div className="px-5 pb-5">
              <p className="text-[11.5px] uppercase tracking-wide font-bold mb-2" style={{ color: 'var(--ink-faint)' }}>
                Mais vendidos
              </p>
              <div className="flex flex-col gap-2">
                {relatorio.produtosMaisVendidos.map((p) => (
                  <div key={p.produtoId} className="flex items-center justify-between text-[13px]">
                    <span>{p.nomeProduto}</span>
                    <span className="num" style={{ color: 'var(--ink-faint)' }}>{p.quantidadeVendida} un. · {moeda(p.valorArrecadado)}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
