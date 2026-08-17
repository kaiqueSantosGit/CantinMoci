# Deploy Gratuito

Tags: #decisao #deploy #infraestrutura

---

## Decisão

Hospedar o backend no **Render** (free tier, via Docker) e o banco de dados no **Neon** (PostgreSQL gerenciado, free tier), sem domínio próprio.

## Motivo

- O sistema é usado esporadicamente em eventos beneficentes — não precisa ficar no ar 24/7
- Objetivo é custo zero, sem depender de uma máquina pessoal ligada como servidor
- Free tiers do Render e Neon não exigem cartão de crédito

## Trade-offs aceitos

- App "dorme" após ~15 min sem tráfego no plano gratuito do Render; a primeira requisição do dia demora ~30-50s pra acordar
- Limite de armazenamento do Postgres gratuito no Neon (adequado para o volume de um evento pequeno)

## Alternativas consideradas

| Opção | Por que não |
|---|---|
| Railway | Free trial expira, passou a exigir cartão |
| Servidor próprio / VPS pago | Foge do objetivo de custo zero |
| Deixar o PC pessoal como servidor | Depende da máquina estar sempre ligada |

## Relacionado
- [[stack]]
- [[fase-4-deploy]]
