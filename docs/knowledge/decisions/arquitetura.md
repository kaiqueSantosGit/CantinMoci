# Arquitetura Backend

Tags: #decisao #arquitetura #backend

---

## Estrutura em camadas

Controller → Service → Repository → Model

### Por que camadas?
Cada camada tem uma responsabilidade única:
- **Controller** — recebe requisição HTTP, devolve resposta
- **Service** — contém as regras de negócio
- **Repository** — faz a comunicação com o banco
- **Model** — representa a entidade no banco
- **DTO** — objeto de transferência entre camadas

### Regra de ouro
Nunca colocar lógica de negócio no Controller.
O Controller só delega para o Service.

## Relacionado
- [[stack]]
- [[JPA]]