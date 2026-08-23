/**
 * Confirmação de ações destrutivas (desativar, encerrar) — modal próprio do
 * app, em vez de window.confirm() nativo.
 *
 * Por que não usar confirm() nativo?
 *   Depois de algumas caixas de diálogo seguidas, o Chrome oferece "impedir
 *   que esta página crie mais caixas de diálogo" — se isso ficar marcado
 *   (as vezes sem querer), TODO confirm() seguinte é cancelado sozinho,
 *   sem nem aparecer na tela. Um modal nosso não depende dessa proteção do
 *   navegador, e ainda fica com a cara do resto do app.
 */
export default function ConfirmDialog({ titulo, mensagem, textoConfirmar = 'Confirmar', perigoso = false, onConfirmar, onCancelar }) {
  return (
    <div className="fixed inset-0 flex items-center justify-center p-4 z-50" style={{ background: 'rgba(0,0,0,0.45)' }}>
      <div className="w-full max-w-sm rounded-2xl p-6" style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}>
        <h3 className="text-base font-semibold mb-2">{titulo}</h3>
        <p className="text-[13.5px] mb-5" style={{ color: 'var(--ink-soft)' }}>{mensagem}</p>
        <div className="flex gap-2">
          <button
            onClick={onCancelar}
            className="flex-1 h-[42px] rounded-[9px] text-sm font-semibold"
            style={{ background: 'var(--surface-sunken)', color: 'var(--ink-soft)' }}
          >
            Cancelar
          </button>
          <button
            onClick={onConfirmar}
            className="flex-1 h-[42px] rounded-[9px] text-sm font-bold"
            style={{ background: perigoso ? 'var(--danger)' : 'var(--brand)', color: 'var(--surface)' }}
          >
            {textoConfirmar}
          </button>
        </div>
      </div>
    </div>
  )
}
