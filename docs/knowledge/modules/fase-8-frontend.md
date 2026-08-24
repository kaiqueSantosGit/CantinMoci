# Fase 8 — Frontend

Tags: #modulo #em-andamento #frontend #react

Status: 🚧 Em andamento (iniciada em 2026-08-23) — todas as telas prontas e testadas (Login, Produtos, Eventos, Vendas, Usuários, Dashboard); deploy na Vercel preparado no código, falta a parte no painel (ver `docs/deploy.md`)

---

## Planejamento visual

Antes de qualquer código, um protótipo navegável (HTML/CSS/JS puro, publicado como Artifact) foi construído e aprovado pelo usuário: telas de Login, Painel, Vendas (PDV com carrinho funcional), Eventos, Produtos e Usuários, com navegação por menu lateral.

**Identidade visual definida:**
- Cor: verde profundo `#1F5245` (marca/navegação) + âmbar `#C97B22` (ação principal, ex: finalizar venda)
- Tipografia: Manrope (interface) + IBM Plex Mono (números/valores — efeito "recibo")
- Layout: menu lateral fixo + barra superior com o evento ativo em destaque
- Tema claro/escuro automático (`prefers-color-scheme`)

## Stack escolhida (e por quê)

| Camada | Escolha | Motivo |
|---|---|---|
| Framework | React 19 + Vite | O estado do carrinho (Vendas) e a troca entre telas se beneficiam de componentes |
| Linguagem | JavaScript (não TypeScript) | Decisão deliberada — não empilhar duas linguagens tipadas novas ao mesmo tempo (o usuário já está aprendendo Java) |
| Roteamento | React Router | Padrão de mercado |
| Estilo | Tailwind CSS 4 | Rápido, reaproveita os tokens do protótipo |
| HTTP | fetch nativo | Suficiente pro tamanho do projeto, sem dependência extra |
| Estado | useState/useContext | Redux/Zustand seriam over-engineering nesse tamanho |
| Deploy (futuro) | Vercel ou Netlify | Free tier, mesma filosofia zero-custo da Fase 4 |

## Configuração no backend (pré-requisito desta fase)

- **CORS**: `CorsConfigurationSource` novo no `SecurityConfig`, origem lida de `CORS_ALLOWED_ORIGINS` (fallback `http://localhost:5173` em dev). Sem isso o navegador bloqueia toda chamada do frontend pra API — Postman/curl nunca foram afetados porque CORS é uma regra do navegador, não da API.
- **`GET /auth/me`** (endpoint novo): o login só devolvia o token — o frontend precisava de uma forma de saber nome/cargo do usuário logado.

## O que foi implementado no frontend

- Projeto `frontend/cantinmoci` (Vite + React)
- `src/index.css` — tokens de cor/tipografia do protótipo, tema claro/escuro
- `src/api/client.js` — cliente HTTP central: anexa o token JWT automaticamente, desloga sozinho em 401
- `src/context/AuthContext.jsx` — sessão do usuário, restaura do token salvo (localStorage) ao recarregar a página via `GET /auth/me`
- `src/components/ProtectedRoute.jsx` — sem sessão válida, redireciona pro login
- `src/layouts/AppShell.jsx` — menu lateral + barra superior; item "Usuários" só aparece pra ADMIN
- `src/pages/Login.jsx` — **funcional de ponta a ponta**, testada no navegador real (CORS, login, `GET /auth/me`, logout, proteção de rota, tema escuro)
- `src/pages/Produtos.jsx` — **funcional de ponta a ponta**: listar (`GET /produtos`), criar/editar via `src/components/ProdutoFormModal.jsx` (`POST`/`PUT`), desativar com confirmação (`DELETE`). Testado no navegador (criar e editar validados via automação; desativar — que abre `confirm()` nativo, não automatizável pela ferramenta de navegador — validado via `curl` direto no endpoint)
- `src/pages/Eventos.jsx` + `src/pages/EventoDetalhe.jsx` — **funcional de ponta a ponta**: lista de eventos, criar (`src/components/EventoFormModal.jsx`), detalhe com estoque alocado (alocar/reforçar via formulário inline), encerrar, e relatório (total, qtd vendas, ticket médio, mais vendidos). Testado no navegador: regra de "1 evento aberto por vez" mostrando a mensagem do backend corretamente, alocação de estoque, relatório com valores conferidos contra os testes da Fase 6
- **`src/components/ConfirmDialog.jsx`** (novo) — modal de confirmação próprio do app, substituindo `window.confirm()` nativo em "desativar produto" e "encerrar evento". Bug real encontrado pelo usuário: depois de várias caixas `confirm()` seguidas, o Chrome oferece "impedir esta página de criar mais diálogos" — se marcado, todo `confirm()` seguinte é cancelado sozinho, sem sequer aparecer. Corrigido eliminando a dependência de diálogos nativos
- `src/pages/Vendas.jsx` (PDV) — **funcional de ponta a ponta**, a tela mais rica:
  - Descobre o evento `ABERTO` sozinho e reaproveita o carrinho `ABERTA` já existente do operador logado (via `usuarioId` + `eventoId`), em vez de abrir um carrinho novo a cada recarregamento de página
  - Grade de produtos mostra "disponível" já descontando o que está no carrinho local (`quantidadeAtual - somaNoCarrinho`), dando feedback antes mesmo de bater no backend
  - Clicar num produto já presente no carrinho soma na mesma linha (`PUT`) em vez de criar uma linha duplicada (`POST`)
  - Erros de estoque insuficiente (400 do backend) aparecem no carrinho sem corromper o estado local
  - Finalizar mostra mensagem de sucesso, atualiza o estoque exibido e já abre um carrinho novo automaticamente
  - Testado no navegador: adicionar, ajustar quantidade (+/−), remover, bloqueio por estoque esgotado (card desabilitado), erro de estoque insuficiente no meio do carrinho, finalizar — com o relatório do evento (tela de Eventos) conferido batendo com o resultado
- `src/pages/Usuarios.jsx` — **funcional de ponta a ponta**, restrita a ADMIN (dupla proteção: item de menu escondido no `AppShell` + guarda de cargo na própria página, caso alguém acesse a URL direto). Lista usuários ativos, cadastra (`UsuarioFormModal.jsx`), reseta senha de outro usuário (`SenhaModal.jsx` reaproveitado), desativa (`ConfirmDialog`) — o botão "Desativar" nem aparece na própria linha do usuário logado, evitando a tentativa que o backend recusaria com 409
- **"Trocar minha senha"** (novo, no rodapé da barra lateral do `AppShell`) — acessível a qualquer cargo, reaproveita o `SenhaModal.jsx` genérico apontando pra `PUT /auth/me/senha` em vez de `PUT /usuarios/{id}/senha`
- Testado no navegador: cadastro, reset de senha (confirmado também via `curl` que a senha nova realmente loga), desativação, e troca da própria senha sem perder a sessão ativa (token JWT não muda ao trocar senha)
- `src/pages/Dashboard.jsx` — **funcional de ponta a ponta**: descobre o evento `ABERTO` sozinho e mostra 4 cartões (arrecadado, quantidade de vendas, ticket médio, itens com estoque baixo — limite de 5 unidades), painel de últimas 6 vendas finalizadas e painel de estoque baixo. Estado vazio ("Nenhum evento aberto", com link pra Eventos) quando não há evento `ABERTO`. Testado no navegador com dados reais (Arrecadado R$24,20, 3 vendas, ticket médio R$8,07)
  - Bug corrigido antes do commit: `setEventoAberto(aberto)` era chamado antes do `Promise.all([relatório, estoque, vendas])` resolver, criando uma janela onde a tela tentava ler `relatorio.valorTotalArrecadado` com `relatorio` ainda `null`. Corrigido guardando tudo atrás de um único `carregando` e só marcando o evento como pronto pra exibir depois que todos os dados chegam juntos
  - Achado (não é bug do Dashboard): ao testar o estado vazio, o clique em "Encerrar evento" pareceu não fazer efeito — investigado com `curl` direto no backend, e a causa real é uma regra de negócio funcionando corretamente: o backend recusa (`409`) encerrar um evento com vendas `ABERTA` (carrinhos abertos) pendentes, e a tela de Eventos já mostra essa mensagem. O evento de teste tinha carrinhos vazios abandonados de sessões de teste anteriores. Registrado em [[backlog]] a falta de um jeito de cancelar/abandonar um carrinho pela UI

## Deploy (Vercel)

Guia completo em `docs/deploy.md`. Resumo da decisão: Vercel escolhida (em
vez de Netlify) por integração mais direta com Vite/React e deploy automático
a cada push. Precisa liberar `CORS_ALLOWED_ORIGINS` no Render pra URL final
da Vercel — sem isso o navegador bloqueia toda chamada à API.

## Decisão de armazenamento do token

`localStorage` — simples, aceitável pro perfil de risco de uma ferramenta interna de time pequeno (não renderizamos HTML de terceiros em lugar nenhum, o que reduz a superfície de XSS). Registrado aqui para revisitar se um dia o projeto crescer pra multi-tenant público (Fase 9).

## Relacionado

- [[fase-3-auth]]
- [[fase-9-multi-tenant]]
- [[arquitetura]]
