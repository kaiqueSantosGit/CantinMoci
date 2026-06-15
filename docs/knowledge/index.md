# CantinMoci — Knowledge Graph

> Rede de conhecimento viva do projeto. Atualizada pelos agentes a cada sessão.
> Leia este arquivo primeiro para entender o estado atual do projeto.

---

## Entidades do sistema
- [[Produto]] — cadastro e controle de estoque
- [[Venda]] — registro de vendas e itens
- [[ItemVenda]] — relação entre venda e produtos
- [[Usuario]] — autenticação e permissões
- [[Evento]] — agrupamento de vendas por evento

---

## Módulos implementados
- [[fase-1-setup]] ✅ — ambiente e endpoint /health
- [[fase-2-produto]] ✅ — CRUD completo de Produto (10 testes Postman validados)

## Módulos futuros
- [[fase-3-auth]] — autenticação JWT
- [[fase-4-vendas]] — sistema de vendas
- [[fase-5-eventos]] — gestão de eventos
- [[fase-6-frontend]] — interface web

---

## Decisões técnicas
- [[stack]] — por que Java 21, Spring Boot, PostgreSQL
- [[arquitetura]] — estrutura em camadas do backend

## Conceitos aprendidos
- [[JPA]] — mapeamento objeto-relacional
- [[Spring-Security]] — autenticação e autorização

---

## Agentes e responsabilidades
| Agente | Lê | Escreve |
|---|---|---|
| mentor | index, learned/ | learned/ |
| backend-dev | index, entities/, decisions/ | entities/, modules/ |
| db-agent | index, entities/ | entities/, decisions/ |
| qa-agent | index, modules/ | modules/ |
| code-reviewer | index, entities/, modules/ | modules/ |
| ui-designer | index, entities/ | modules/ |
| frontend-dev | index, entities/, modules/ | modules/ |