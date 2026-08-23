# Fase 8 — Frontend

Tags: #modulo #em-andamento #frontend #react

Status: 🚧 Em andamento (iniciada em 2026-08-23) — setup, CORS e login funcionando; telas dos módulos ainda por construir

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
- `src/pages/Eventos.jsx` + `src/pages/EventoDetalhe.jsx` — **funcional de ponta a ponta**: lista de eventos, criar (`src/components/EventoFormModal.jsx`), detalhe com estoque alocado (alocar/reforçar via formulário inline), encerrar (`confirm()` nativo, não automatizável — confiança no mesmo padrão já provado no `desativar` de Produtos), e relatório (total, qtd vendas, ticket médio, mais vendidos). Testado no navegador: regra de "1 evento aberto por vez" mostrando a mensagem do backend corretamente, alocação de estoque, relatório com valores conferidos contra os testes da Fase 6
- Demais páginas (Dashboard, Vendas, Usuarios) — placeholders "em construção" por enquanto, uma tela de cada vez nas próximas etapas

## Decisão de armazenamento do token

`localStorage` — simples, aceitável pro perfil de risco de uma ferramenta interna de time pequeno (não renderizamos HTML de terceiros em lugar nenhum, o que reduz a superfície de XSS). Registrado aqui para revisitar se um dia o projeto crescer pra multi-tenant público (Fase 9).

## Relacionado

- [[fase-3-auth]]
- [[fase-9-multi-tenant]]
- [[arquitetura]]
