import { useEffect, useState } from 'react'
import { listarProdutos, desativarProduto } from '../api/produtos'
import ProdutoFormModal from '../components/ProdutoFormModal'

export default function Produtos() {
  const [produtos, setProdutos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  // null = modal fechado. 'novo' = criando. um objeto produto = editando.
  const [modal, setModal] = useState(null)

  async function carregar() {
    setCarregando(true)
    setErro('')
    try {
      const dados = await listarProdutos()
      setProdutos(dados)
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
  }

  useEffect(() => {
    carregar()
  }, [])

  async function handleDesativar(produto) {
    const confirmou = window.confirm(`Desativar "${produto.nome}"? Ele deixa de aparecer nas vendas, mas o histórico é preservado.`)
    if (!confirmou) return

    try {
      await desativarProduto(produto.id)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  function fecharModal() {
    setModal(null)
  }

  function handleSalvo() {
    setModal(null)
    carregar()
  }

  function moeda(valor) {
    return 'R$ ' + Number(valor).toFixed(2).replace('.', ',')
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <span className="text-[13px]" style={{ color: 'var(--ink-faint)' }}>
          {carregando ? 'Carregando…' : `${produtos.length} produto${produtos.length === 1 ? '' : 's'} ativo${produtos.length === 1 ? '' : 's'}`}
        </span>
        <button
          onClick={() => setModal('novo')}
          className="h-9 px-3.5 rounded-lg text-[13px] font-bold"
          style={{ background: 'var(--brand-tint)', color: 'var(--brand-strong)', border: '1px solid var(--brand)' }}
        >
          + Novo produto
        </button>
      </div>

      {erro && (
        <p className="text-[13px] mb-3 px-1" style={{ color: 'var(--danger)' }}>
          {erro}
        </p>
      )}

      <div className="rounded-xl overflow-hidden" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
        {!carregando && produtos.length === 0 && !erro ? (
          <div className="text-center py-10">
            <p className="text-sm font-semibold mb-1">Nenhum produto cadastrado</p>
            <p className="text-[13px] m-0" style={{ color: 'var(--ink-faint)' }}>
              Clique em "+ Novo produto" pra cadastrar o primeiro.
            </p>
          </div>
        ) : (
          <table className="w-full text-[13.5px]" style={{ tableLayout: 'fixed' }}>
            <colgroup>
              <col style={{ width: '46%' }} />
              <col style={{ width: '18%' }} />
              <col style={{ width: '18%' }} />
              <col style={{ width: '18%' }} />
            </colgroup>
            <thead>
              <tr>
                <th className="text-left text-[11.5px] uppercase tracking-wide font-bold py-2.5 px-5" style={{ color: 'var(--ink-faint)', borderBottom: '1px solid var(--line)' }}>Produto</th>
                <th className="text-left text-[11.5px] uppercase tracking-wide font-bold py-2.5 px-5" style={{ color: 'var(--ink-faint)', borderBottom: '1px solid var(--line)' }}>Preço</th>
                <th className="text-left text-[11.5px] uppercase tracking-wide font-bold py-2.5 px-5" style={{ color: 'var(--ink-faint)', borderBottom: '1px solid var(--line)' }}>Estoque</th>
                <th className="text-left text-[11.5px] uppercase tracking-wide font-bold py-2.5 px-5" style={{ color: 'var(--ink-faint)', borderBottom: '1px solid var(--line)' }}></th>
              </tr>
            </thead>
            <tbody>
              {produtos.map((produto) => (
                <tr key={produto.id}>
                  <td className="py-3 px-5" style={{ borderBottom: '1px solid var(--line)' }}>{produto.nome}</td>
                  <td className="py-3 px-5 num" style={{ borderBottom: '1px solid var(--line)' }}>{moeda(produto.preco)}</td>
                  <td className="py-3 px-5 num" style={{ borderBottom: '1px solid var(--line)' }}>{produto.quantidadeEmEstoque}</td>
                  <td className="py-3 px-5 text-right" style={{ borderBottom: '1px solid var(--line)' }}>
                    <button onClick={() => setModal(produto)} className="text-[12.5px] font-semibold mr-3" style={{ color: 'var(--brand-strong)' }}>
                      Editar
                    </button>
                    <button onClick={() => handleDesativar(produto)} className="text-[12.5px] font-semibold" style={{ color: 'var(--danger)' }}>
                      Desativar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {modal && (
        <ProdutoFormModal
          produto={modal === 'novo' ? null : modal}
          onFechar={fecharModal}
          onSalvo={handleSalvo}
        />
      )}
    </div>
  )
}
