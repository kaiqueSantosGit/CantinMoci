import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { listarUsuarios, desativarUsuario, resetarSenha } from '../api/usuarios'
import UsuarioFormModal from '../components/UsuarioFormModal'
import SenhaModal from '../components/SenhaModal'
import ConfirmDialog from '../components/ConfirmDialog'

export default function Usuarios() {
  const { usuario: usuarioLogado } = useAuth()

  const [usuarios, setUsuarios] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  const [modalNovo, setModalNovo] = useState(false)
  const [paraResetarSenha, setParaResetarSenha] = useState(null)
  const [paraDesativar, setParaDesativar] = useState(null)

  async function carregar() {
    setCarregando(true)
    setErro('')
    try {
      const dados = await listarUsuarios()
      setUsuarios(dados)
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
  }

  useEffect(() => {
    carregar()
  }, [])

  function handleCadastrado() {
    setModalNovo(false)
    carregar()
  }

  async function handleResetarSenha(novaSenha) {
    await resetarSenha(paraResetarSenha.id, { novaSenha })
    setParaResetarSenha(null)
  }

  async function handleDesativar() {
    const alvo = paraDesativar
    setParaDesativar(null)
    try {
      await desativarUsuario(alvo.id)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  // A tela de gestão de usuários é restrita a ADMIN (mesma regra do
  // backend) — o menu já esconde o link pra quem não é ADMIN, isso aqui
  // é só uma segunda camada de proteção caso alguém acesse a URL direto.
  if (usuarioLogado.cargo !== 'ADMIN') {
    return (
      <div className="rounded-xl p-10 text-center" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
        <p className="text-sm font-semibold m-0">Acesso restrito a administradores.</p>
      </div>
    )
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <span className="text-[13px]" style={{ color: 'var(--ink-faint)' }}>
          {carregando ? 'Carregando…' : `${usuarios.length} usuário${usuarios.length === 1 ? '' : 's'} ativo${usuarios.length === 1 ? '' : 's'}`}
        </span>
        <button
          onClick={() => setModalNovo(true)}
          className="h-9 px-3.5 rounded-lg text-[13px] font-bold"
          style={{ background: 'var(--brand-tint)', color: 'var(--brand-strong)', border: '1px solid var(--brand)' }}
        >
          + Novo usuário
        </button>
      </div>

      {erro && <p className="text-[13px] mb-3 px-1" style={{ color: 'var(--danger)' }}>{erro}</p>}

      <div className="rounded-xl overflow-hidden" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
        <table className="w-full text-[13.5px]" style={{ tableLayout: 'fixed' }}>
          <colgroup>
            <col style={{ width: '28%' }} />
            <col style={{ width: '34%' }} />
            <col style={{ width: '15%' }} />
            <col style={{ width: '23%' }} />
          </colgroup>
          <thead>
            <tr>
              <th className="text-left text-[11.5px] uppercase tracking-wide font-bold py-2.5 px-5" style={{ color: 'var(--ink-faint)', borderBottom: '1px solid var(--line)' }}>Nome</th>
              <th className="text-left text-[11.5px] uppercase tracking-wide font-bold py-2.5 px-5" style={{ color: 'var(--ink-faint)', borderBottom: '1px solid var(--line)' }}>Email</th>
              <th className="text-left text-[11.5px] uppercase tracking-wide font-bold py-2.5 px-5" style={{ color: 'var(--ink-faint)', borderBottom: '1px solid var(--line)' }}>Cargo</th>
              <th className="text-left text-[11.5px] uppercase tracking-wide font-bold py-2.5 px-5" style={{ color: 'var(--ink-faint)', borderBottom: '1px solid var(--line)' }}></th>
            </tr>
          </thead>
          <tbody>
            {usuarios.map((u) => {
              const souEu = u.id === usuarioLogado.id
              return (
                <tr key={u.id}>
                  <td className="py-3 px-5" style={{ borderBottom: '1px solid var(--line)' }}>
                    {u.nome}{souEu && <span style={{ color: 'var(--ink-faint)' }}> (você)</span>}
                  </td>
                  <td className="py-3 px-5" style={{ borderBottom: '1px solid var(--line)', color: 'var(--ink-soft)' }}>{u.email}</td>
                  <td className="py-3 px-5" style={{ borderBottom: '1px solid var(--line)' }}>
                    <span
                      className="inline-flex items-center h-[22px] px-2.5 rounded-full text-[11.5px] font-bold"
                      style={{ background: 'var(--brand-tint)', color: 'var(--brand-strong)' }}
                    >
                      {u.cargo}
                    </span>
                  </td>
                  <td className="py-3 px-5 text-right" style={{ borderBottom: '1px solid var(--line)' }}>
                    <button onClick={() => setParaResetarSenha(u)} className="text-[12.5px] font-semibold mr-3" style={{ color: 'var(--brand-strong)' }}>
                      Resetar senha
                    </button>
                    {!souEu && (
                      <button onClick={() => setParaDesativar(u)} className="text-[12.5px] font-semibold" style={{ color: 'var(--danger)' }}>
                        Desativar
                      </button>
                    )}
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      </div>

      {modalNovo && <UsuarioFormModal onFechar={() => setModalNovo(false)} onSalvo={handleCadastrado} />}

      {paraResetarSenha && (
        <SenhaModal
          titulo="Resetar senha"
          subtitulo={`Definir uma nova senha para ${paraResetarSenha.nome}.`}
          onFechar={() => setParaResetarSenha(null)}
          onSalvar={handleResetarSenha}
        />
      )}

      {paraDesativar && (
        <ConfirmDialog
          titulo="Desativar usuário"
          mensagem={`Desativar "${paraDesativar.nome}"? Ele perde acesso ao sistema imediatamente, mesmo com um token ainda válido.`}
          textoConfirmar="Desativar"
          perigoso
          onCancelar={() => setParaDesativar(null)}
          onConfirmar={handleDesativar}
        />
      )}
    </div>
  )
}
