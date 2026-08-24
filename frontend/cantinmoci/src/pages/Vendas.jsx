import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { listarEventos, listarProdutosDoEvento } from '../api/eventos'
import { listarProdutos } from '../api/produtos'
import { abrirVenda, listarVendas, adicionarItem, atualizarQuantidade, removerItem, finalizarVenda } from '../api/vendas'

function moeda(valor) {
  return 'R$ ' + Number(valor).toFixed(2).replace('.', ',')
}

export default function Vendas() {
  const { usuario } = useAuth()

  const [eventoAberto, setEventoAberto] = useState(undefined) // undefined = ainda não sabemos, null = não há nenhum
  const [estoqueEvento, setEstoqueEvento] = useState([])
  const [catalogo, setCatalogo] = useState([])
  const [venda, setVenda] = useState(null)
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [erroCarrinho, setErroCarrinho] = useState('')
  const [finalizando, setFinalizando] = useState(false)
  const [mensagemSucesso, setMensagemSucesso] = useState('')
  // Só usado no celular — o carrinho vira uma barra fixa no rodapé que expande
  // ao tocar, em vez de ficar sempre visível do lado (não tem espaço pra isso).
  const [carrinhoAberto, setCarrinhoAberto] = useState(false)

  async function carregarEstoque(eventoId) {
    const [estoque, prods] = await Promise.all([listarProdutosDoEvento(eventoId), listarProdutos()])
    setCatalogo(prods)
    const precoPorId = Object.fromEntries(prods.map((p) => [p.id, p.preco]))
    setEstoqueEvento(estoque.map((e) => ({ ...e, preco: precoPorId[e.produtoId] })))
  }

  async function carregar() {
    setCarregando(true)
    setErro('')
    try {
      const eventos = await listarEventos()
      const aberto = eventos.find((e) => e.status === 'ABERTO')

      if (!aberto) {
        setEventoAberto(null)
        setCarregando(false)
        return
      }
      setEventoAberto(aberto)

      await carregarEstoque(aberto.id)

      // Reaproveita um carrinho já aberto por este usuário neste evento,
      // em vez de abrir um novo toda vez que a tela recarrega.
      const vendasAbertas = await listarVendas('ABERTA')
      const minhaVenda = vendasAbertas.find((v) => v.usuarioId === usuario.id && v.eventoId === aberto.id)
      const vendaAtual = minhaVenda ?? (await abrirVenda())
      setVenda(vendaAtual)
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
  }

  useEffect(() => {
    carregar()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  function estoqueDisponivel(produtoId) {
    const item = estoqueEvento.find((e) => e.produtoId === produtoId)
    if (!item) return 0
    const noCarrinho = venda?.itens.filter((i) => i.produtoId === produtoId).reduce((soma, i) => soma + i.quantidade, 0) ?? 0
    return item.quantidadeAtual - noCarrinho
  }

  async function handleAdicionar(produtoId) {
    setErroCarrinho('')
    const itemExistente = venda.itens.find((i) => i.produtoId === produtoId)
    try {
      const atualizada = itemExistente
        ? await atualizarQuantidade(venda.id, itemExistente.id, { quantidade: itemExistente.quantidade + 1 })
        : await adicionarItem(venda.id, { produtoId, quantidade: 1 })
      setVenda(atualizada)
    } catch (e) {
      setErroCarrinho(e.message)
    }
  }

  async function handleDiminuir(item) {
    setErroCarrinho('')
    try {
      const atualizada =
        item.quantidade > 1
          ? await atualizarQuantidade(venda.id, item.id, { quantidade: item.quantidade - 1 })
          : await removerItem(venda.id, item.id)
      setVenda(atualizada)
    } catch (e) {
      setErroCarrinho(e.message)
    }
  }

  async function handleRemover(item) {
    setErroCarrinho('')
    try {
      const atualizada = await removerItem(venda.id, item.id)
      setVenda(atualizada)
    } catch (e) {
      setErroCarrinho(e.message)
    }
  }

  async function handleFinalizar() {
    setFinalizando(true)
    setErroCarrinho('')
    try {
      const vendaFinalizada = await finalizarVenda(venda.id)
      setMensagemSucesso(`Venda #${vendaFinalizada.id} finalizada — ${moeda(vendaFinalizada.valorTotal)}`)
      await carregarEstoque(eventoAberto.id)
      const novaVenda = await abrirVenda()
      setVenda(novaVenda)
      setCarrinhoAberto(false)
      setTimeout(() => setMensagemSucesso(''), 3500)
    } catch (e) {
      setErroCarrinho(e.message)
    } finally {
      setFinalizando(false)
    }
  }

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
          Abra um evento e aloque estoque de produtos nele antes de iniciar vendas.
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

  // Conteúdo do carrinho — reaproveitado entre o painel fixo do desktop e a
  // gaveta que sobe do rodapé no celular, pra não duplicar essa lógica.
  const conteudoCarrinho = (
    <>
      <div className="px-[18px] py-1 min-h-[80px]">
        {venda.itens.length === 0 ? (
          <p className="text-[12.5px] text-center py-6 m-0" style={{ color: 'var(--ink-faint)' }}>
            Toque num produto pra adicionar
          </p>
        ) : (
          venda.itens.map((item) => (
            <div key={item.id} className="flex items-center justify-between py-2.5" style={{ borderBottom: '1px solid var(--line)' }}>
              <div className="min-w-0">
                <p className="text-[13px] font-semibold m-0 truncate">{item.nomeProduto}</p>
                <p className="text-[12.5px] num m-0" style={{ color: 'var(--ink-faint)' }}>{moeda(item.subtotal)}</p>
              </div>
              <div className="flex items-center gap-1.5 shrink-0">
                <button onClick={() => handleDiminuir(item)} className="w-7 h-7 md:w-6 md:h-6 rounded-md text-sm font-bold" style={{ background: 'var(--surface-sunken)' }}>−</button>
                <span className="text-[13px] font-semibold w-4 text-center num">{item.quantidade}</span>
                <button onClick={() => handleAdicionar(item.produtoId)} className="w-7 h-7 md:w-6 md:h-6 rounded-md text-sm font-bold" style={{ background: 'var(--surface-sunken)' }}>+</button>
                <button onClick={() => handleRemover(item)} className="text-[11px] font-semibold ml-1" style={{ color: 'var(--danger)' }}>remover</button>
              </div>
            </div>
          ))
        )}
      </div>

      {erroCarrinho && (
        <p className="text-[12.5px] px-[18px] pt-2 m-0" style={{ color: 'var(--danger)' }}>{erroCarrinho}</p>
      )}

      <div className="flex items-baseline justify-between px-[18px] py-4" style={{ borderTop: '1px solid var(--line)' }}>
        <span className="text-[12.5px] font-semibold" style={{ color: 'var(--ink-faint)' }}>Total</span>
        <span className="text-xl font-semibold num">{moeda(venda.valorTotal)}</span>
      </div>

      <button
        onClick={handleFinalizar}
        disabled={venda.itens.length === 0 || finalizando}
        className="mx-[18px] mb-[18px] h-[46px] md:h-[42px] rounded-[9px] text-[13.5px] font-bold disabled:opacity-50"
        style={{ background: 'var(--accent)', color: '#fff' }}
      >
        {finalizando ? 'Finalizando…' : 'Finalizar venda'}
      </button>
    </>
  )

  const totalItens = venda.itens.reduce((soma, i) => soma + i.quantidade, 0)

  return (
    <div className="pb-24 md:pb-0">
      {mensagemSucesso && (
        <div className="rounded-lg px-4 py-2.5 mb-4 text-[13.5px] font-semibold" style={{ background: 'var(--success-tint)', color: 'var(--success)' }}>
          {mensagemSucesso}
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-[minmax(0,1fr)_300px] gap-5">
        <div className="grid gap-2.5" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(140px, 1fr))' }}>
          {estoqueEvento.length === 0 ? (
            <p className="text-[13px] col-span-full" style={{ color: 'var(--ink-faint)' }}>
              Nenhum produto alocado nesse evento ainda —{' '}
              <Link to={`/eventos/${eventoAberto.id}`} style={{ color: 'var(--brand-strong)', fontWeight: 600 }}>
                aloque estoque na tela do evento
              </Link>.
            </p>
          ) : (
            estoqueEvento.map((item) => {
              const disponivel = estoqueDisponivel(item.produtoId)
              const esgotado = disponivel <= 0
              return (
                <button
                  key={item.produtoId}
                  disabled={esgotado}
                  onClick={() => handleAdicionar(item.produtoId)}
                  className="rounded-xl p-3.5 text-left flex flex-col gap-1.5 disabled:opacity-50"
                  style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}
                >
                  <span className="text-[13.5px] font-bold">{item.nomeProduto}</span>
                  <span className="text-[15px] font-semibold num" style={{ color: 'var(--brand-strong)' }}>{moeda(item.preco)}</span>
                  <span className="text-[11.5px]" style={{ color: 'var(--ink-faint)' }}>
                    {esgotado ? 'sem estoque' : `${disponivel} disponíveis`}
                  </span>
                </button>
              )
            })
          )}
        </div>

        {/* Desktop: carrinho sempre visível ao lado */}
        <div className="hidden md:flex rounded-xl flex-col" style={{ background: 'var(--surface)', border: '1px solid var(--line)', alignSelf: 'start' }}>
          <div className="px-[18px] py-4" style={{ borderBottom: '1px solid var(--line)' }}>
            <h3 className="text-[14.5px] font-semibold m-0">Carrinho — venda #{venda.id}</h3>
          </div>
          {conteudoCarrinho}
        </div>
      </div>

      {/* Celular: barra fixa no rodapé com o resumo, toca pra expandir */}
      <button
        onClick={() => setCarrinhoAberto(true)}
        className="md:hidden fixed bottom-0 inset-x-0 z-40 flex items-center justify-between px-5 h-[64px]"
        style={{ background: 'var(--brand)', color: 'var(--surface)', boxShadow: '0 -4px 16px rgba(0,0,0,0.15)' }}
      >
        <span className="text-[13.5px] font-semibold">
          {totalItens === 0 ? 'Carrinho vazio' : `${totalItens} ${totalItens === 1 ? 'item' : 'itens'} · Ver carrinho`}
        </span>
        <span className="text-[17px] font-bold num">{moeda(venda.valorTotal)}</span>
      </button>

      {/* Celular: gaveta do carrinho, sobe do rodapé */}
      {carrinhoAberto && (
        <div className="md:hidden fixed inset-0 z-50 flex items-end">
          <div className="absolute inset-0" style={{ background: 'rgba(0,0,0,0.45)' }} onClick={() => setCarrinhoAberto(false)} />
          <div
            className="relative w-full max-h-[85vh] flex flex-col rounded-t-2xl overflow-y-auto"
            style={{ background: 'var(--surface)', border: '1px solid var(--line)', borderBottom: 'none' }}
          >
            <div className="flex items-center justify-between px-[18px] py-4" style={{ borderBottom: '1px solid var(--line)' }}>
              <h3 className="text-[14.5px] font-semibold m-0">Carrinho — venda #{venda.id}</h3>
              <button onClick={() => setCarrinhoAberto(false)} aria-label="Fechar carrinho" className="text-[13px] font-semibold" style={{ color: 'var(--ink-faint)' }}>
                Fechar
              </button>
            </div>
            {conteudoCarrinho}
          </div>
        </div>
      )}
    </div>
  )
}
