// Placeholder das telas que ainda não foram construídas — cada uma vira um
// módulo próprio nas próximas etapas (mesmo ritmo usado no backend: uma
// tela de cada vez, testada antes de seguir pra próxima).
export default function EmConstrucao({ titulo }) {
  return (
    <div
      className="rounded-xl p-10 text-center"
      style={{ background: 'var(--surface)', border: '1px solid var(--line)' }}
    >
      <h3 className="text-base font-semibold mb-1.5">{titulo}</h3>
      <p className="text-sm m-0" style={{ color: 'var(--ink-faint)' }}>
        Esta tela ainda não foi construída — chega numa próxima etapa.
      </p>
    </div>
  )
}
