import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { listarEventos, listarProdutosDoEvento, obterRelatorio } from '../api/eventos'
import { listarVendas } from '../api/vendas'

const ESTOQUE_BAIXO_LIMITE = 5

function moeda(valor) {
  return 'R$ ' + Number(valor).toFixed(2).replace('.', ',')
}

function hora(iso) {
  return new Date(iso).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })
}

export default function Dashboard() {
  const [carregando, setCarregando] = useState(true)
  const [eventoAberto, setEventoAberto] = useState(null)
  const [relatorio, setRelatorio] = useState(null)
  const [estoqueBaixo, setEstoqueBaixo] = useState([])
  const [ultimasVendas, setUltimasVendas] = useState([])
  const [erro, setErro] = useState('')

  useEffect(() => {
    async function carregar() {
      setCarregando(true)
      setErro('')
      try {
        const eventos = await listarEventos()
        const aberto = eventos.find((e) => e.status === 'ABERTO')

        if (!aberto) {
          setEventoAberto(null)
          return
        }

        // Só marca o evento como "pronto pra exibir" DEPOIS que o
        // relatório também chegou — senão existe uma janela de tempo em
        // que eventoAberto já está preenchido mas relatorio ainda é null,
        // e a tela tentaria ler relatorio.valorTotalArrecadado de null.
        const [dadosRelatorio, estoque, vendas] = await Promise.all([
          obterRelatorio(aberto.id),
          listarProdutosDoEvento(aberto.id),
          listarVendas('FINALIZADA'),
        ])

        setEventoAberto(aberto)
        setRelatorio(dadosRelatorio)
        setEstoqueBaixo(estoque.filter((e) => e.quantidadeAtual <= ESTOQUE_BAIXO_LIMITE))

        const vendasDoEvento = vendas
          .filter((v) => v.eventoId === aberto.id)
          .sort((a, b) => new Date(b.dataFinalizacao) - new Date(a.dataFinalizacao))
          .slice(0, 6)
        setUltimasVendas(vendasDoEvento)
      } catch (e) {
        setErro(e.message)
      } finally {
        setCarregando(false)
      }
    }
    carregar()
  }, [])

  if (carregando) {
    return <p className="text-sm" style={{ color: 'var(--ink-faint)' }}>Carregando…</p>
  }

  if (erro) {
    return <p className="text-sm" style={{ color: 'var(--danger)' }}>{erro}</p>
  }

  if (!eventoAberto) {
    return (
      <div className="rounded-xl p-10 text-center" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
        <h3 className="text-base font-semibold mb-1.5">Nenhum evento aberto</h3>
        <p className="text-sm mb-4" style={{ color: 'var(--ink-faint)' }}>
          Abra um evento pra começar a acompanhar vendas em tempo real aqui.
        </p>
        <Link
          to="/eventos"
          className="inline-block h-9 px-4 leading-9 rounded-lg text-[13px] font-bold"
          style={{ background: 'var(--brand-tint)', color: 'var(--brand-strong)', border: '1px solid var(--brand)' }}
        >
          Ir para Eventos
        </Link>
      </div>
    )
  }

  return (
    <div>
      <div className="rounded-xl p-5 mb-5" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
        <h3 className="text-base font-semibold mb-1">{eventoAberto.nome}</h3>
        <p className="text-[13px] m-0" style={{ color: 'var(--ink-faint)' }}>
          {eventoAberto.local && <>{eventoAberto.local} · </>}
          <span className="font-bold" style={{ color: 'var(--success)' }}>Aberto</span>
        </p>
      </div>

      <div className="grid gap-2.5 mb-5" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))' }}>
        <div className="rounded-xl p-4" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
          <div className="text-[12.5px] font-semibold" style={{ color: 'var(--ink-faint)' }}>Arrecadado</div>
          <div className="text-2xl font-semibold num mt-1.5" style={{ color: 'var(--brand-strong)' }}>{moeda(relatorio.valorTotalArrecadado)}</div>
        </div>
        <div className="rounded-xl p-4" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
          <div className="text-[12.5px] font-semibold" style={{ color: 'var(--ink-faint)' }}>Vendas</div>
          <div className="text-2xl font-semibold num mt-1.5">{relatorio.quantidadeVendas}</div>
        </div>
        <div className="rounded-xl p-4" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
          <div className="text-[12.5px] font-semibold" style={{ color: 'var(--ink-faint)' }}>Ticket médio</div>
          <div className="text-2xl font-semibold num mt-1.5">{moeda(relatorio.ticketMedio)}</div>
        </div>
        <div className="rounded-xl p-4" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
          <div className="text-[12.5px] font-semibold" style={{ color: 'var(--ink-faint)' }}>Estoque baixo</div>
          <div className="text-2xl font-semibold num mt-1.5" style={{ color: estoqueBaixo.length > 0 ? 'var(--danger)' : 'var(--ink)' }}>
            {estoqueBaixo.length}
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
        <div className="rounded-xl overflow-hidden" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
          <div className="px-5 py-3.5" style={{ borderBottom: '1px solid var(--line)' }}>
            <h4 className="text-[14.5px] font-semibold m-0">Últimas vendas</h4>
          </div>
          {ultimasVendas.length === 0 ? (
            <p className="text-[13px] text-center py-6 m-0" style={{ color: 'var(--ink-faint)' }}>Nenhuma venda finalizada ainda.</p>
          ) : (
            ultimasVendas.map((v) => (
              <div key={v.id} className="flex items-center justify-between px-5 py-2.5 text-[13px]" style={{ borderBottom: '1px solid var(--line)' }}>
                <span>Venda #{v.id} · {v.nomeUsuario}</span>
                <span className="num" style={{ color: 'var(--ink-faint)' }}>{moeda(v.valorTotal)} · {hora(v.dataFinalizacao)}</span>
              </div>
            ))
          )}
        </div>

        <div className="rounded-xl overflow-hidden" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
          <div className="px-5 py-3.5" style={{ borderBottom: '1px solid var(--line)' }}>
            <h4 className="text-[14.5px] font-semibold m-0">Estoque baixo</h4>
          </div>
          {estoqueBaixo.length === 0 ? (
            <p className="text-[13px] text-center py-6 m-0" style={{ color: 'var(--ink-faint)' }}>Nenhum produto com estoque baixo.</p>
          ) : (
            estoqueBaixo.map((e) => (
              <div key={e.produtoId} className="flex items-center justify-between px-5 py-2.5 text-[13px]" style={{ borderBottom: '1px solid var(--line)' }}>
                <span>{e.nomeProduto}</span>
                <span className="font-bold num" style={{ color: e.quantidadeAtual === 0 ? 'var(--danger)' : 'var(--accent)' }}>
                  {e.quantidadeAtual === 0 ? 'esgotado' : `${e.quantidadeAtual} restantes`}
                </span>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  )
}
