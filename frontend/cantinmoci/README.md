# CantinMoci — Frontend

Interface web do CantinMoci. React + Vite + React Router + Tailwind CSS,
consumindo a API REST do backend (`backend/cantinmoci`).

## Como rodar localmente

Pré-requisito: o backend precisa estar rodando em `http://localhost:8080`
(ver `backend/cantinmoci/README` / `DEV_LOG.md` na raiz do projeto).

```bash
cd frontend/cantinmoci
npm install
npm run dev
# Sobe em http://localhost:5173
```

A URL da API vem da variável `VITE_API_URL` (arquivo `.env.development`,
já configurada para `http://localhost:8080`).

## Estrutura de pastas

```
src/
  api/         cliente HTTP central (fetch + token JWT automático)
  context/     AuthContext — sessão do usuário logado
  components/  componentes reutilizáveis (ProtectedRoute, EmConstrucao)
  layouts/     AppShell — menu lateral + barra superior
  pages/       uma tela por módulo (Login, Dashboard, Vendas, Eventos, Produtos, Usuarios)
```

## Identidade visual

Definida a partir de um protótipo aprovado antes do código (ver
`docs/knowledge/modules/fase-8-frontend.md` na raiz do projeto). Os tokens
de cor (`--brand`, `--accent`, `--paper` etc.) ficam em `src/index.css`,
com suporte a tema claro/escuro automático (`prefers-color-scheme`).

## Build de produção

```bash
npm run build
# gera frontend/cantinmoci/dist/
```

Deploy planejado via Vercel ou Netlify (free tier) — ainda não configurado.
