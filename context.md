# CantinMoci - Project Context

# Visão Geral do Projeto

## Nome do Projeto

CantinMoci

---

## Objetivo do Sistema

O CantinMoci será um sistema web de gerenciamento de vendas, estoque, caixa e organização de eventos beneficentes, desenvolvido inicialmente como projeto de estudo e evolução técnica, mas com possibilidade futura de utilização prática em eventos reais e centros beneficentes.

O principal objetivo do sistema é facilitar o gerenciamento operacional de cantinas e pontos de venda durante eventos, oferecendo uma estrutura simples, organizada e eficiente para controle de produtos, estoque, vendas e acompanhamento financeiro básico.

O sistema deverá permitir que usuários criem uma conta e realizem autenticação através de login seguro. Após o login, o usuário terá acesso a um dashboard principal de eventos, onde poderá:

* criar novos eventos
* visualizar eventos já realizados
* editar eventos existentes
* acessar informações individuais de cada evento
* gerenciar vendas e estoque separadamente por evento

Cada evento funcionará como um ambiente independente dentro do sistema, possuindo seus próprios produtos, estoque, histórico de vendas e informações organizacionais.

---

## Gerenciamento de Eventos

Ao criar um novo evento, o usuário poderá personalizar diversas informações importantes para organização do ambiente de vendas.

Cada evento poderá possuir:

* nome do evento
* data de realização
* descrição do evento
* foto de capa do evento (opcional)
* status do evento
* informações adicionais futuras

O sistema deverá organizar automaticamente toda a estrutura operacional daquele evento específico.

---

## Sistema de Produtos e Estoque

Dentro de cada evento, o usuário poderá cadastrar e gerenciar os produtos que serão vendidos durante o funcionamento da cantina ou ponto de venda.

Cada produto poderá possuir:

* nome
* descrição
* categoria
* quantidade em estoque
* valor de compra
* valor de revenda
* status de disponibilidade
* imagem do produto futuramente

O sistema deverá realizar automaticamente o controle de estoque conforme as vendas forem realizadas.

Também deverão existir funcionalidades como:

* entrada manual de estoque
* atualização de quantidade
* edição de preços
* remoção de produtos
* alertas de estoque baixo
* visualização rápida da quantidade restante

---

## Sistema de Vendas

O sistema possuirá uma interface de vendas rápida e intuitiva, focada em facilitar o atendimento durante eventos movimentados.

O operador poderá:

* visualizar produtos disponíveis
* selecionar produtos rapidamente
* adicionar itens ao carrinho
* alterar quantidades
* remover produtos da venda
* visualizar subtotal e total em tempo real
* finalizar vendas

O sistema deverá realizar automaticamente:

* cálculo dos valores
* soma total da venda
* atualização do estoque
* registro do histórico da venda
* armazenamento de data e horário da operação

Cada venda deverá ficar vinculada:

* ao evento
* ao usuário responsável
* à data da operação

---

## Dashboard e Controle Administrativo

O sistema deverá possuir dashboards e áreas administrativas para acompanhamento do evento em tempo real.

Algumas informações previstas:

* total arrecadado
* quantidade de vendas
* produtos mais vendidos
* estoque restante
* eventos ativos
* alertas operacionais
* histórico de movimentações

---

## Funcionalidades Principais

O sistema deverá permitir:

* Login e autenticação de usuários
* Configuração e gerenciamento de eventos
* Controle de produtos
* Controle de estoque
* Sistema de vendas
* Gerenciamento de usuários
* Relatórios básicos
* Organização operacional de eventos

---

## Objetivo Técnico do Projeto

Além da utilização prática, o projeto também terá como objetivo servir como ferramenta de aprendizado e evolução técnica no desenvolvimento de software.

O desenvolvimento do sistema será utilizado para aprofundamento prático em:

* Java
* Spring Boot
* APIs REST
* SQL
* PostgreSQL
* Arquitetura Backend
* Modelagem de Banco de Dados
* Relacionamentos entre tabelas
* Controle de estoque
* Sistemas de vendas
* Autenticação e segurança
* Git e GitHub
* Frontend
* UX/UI
* Deploy e produção

O projeto será desenvolvido de forma modular, organizada e escalável, priorizando aprendizado gradual, entendimento completo de cada funcionalidade e evolução contínua da arquitetura do sistema.

Mais funcionalidades e contextos poderão ser adicionados e atualizados futuramente durante a evolução do projeto.


---

# Objetivos de Aprendizado

Este projeto também será utilizado como ferramenta de aprendizado prático em:

* Java
* Spring Boot
* APIs REST
* SQL
* PostgreSQL
* Arquitetura Backend
* Modelagem de Banco de Dados
* Git e GitHub
* Versionamento
* Estruturação de Sistemas
* Frontend
* UX/UI
* Deploy

---

# Regras de Desenvolvimento do Projeto

## Regras Gerais

* O projeto será desenvolvido por etapas.
* Cada módulo deve ser entendido antes de avançar.
* Nenhuma funcionalidade deve ser criada sem documentação mínima.
* O foco é aprendizado + construção real.
* Evitar criar muitas funcionalidades ao mesmo tempo.
* Priorizar simplicidade antes de complexidade.

---

## Regras para Uso de IA

A IA deve atuar como:

* assistente técnico
* mentor
* apoio de arquitetura
* explicador de código

A IA NÃO deve:

* gerar o sistema inteiro de uma vez
* pular etapas
* criar código sem explicação
* modificar arquitetura sem contexto

---

## Fluxo Obrigatório de Desenvolvimento

Cada funcionalidade deve seguir:

1. Planejamento
2. Modelagem
3. Backend
4. Banco de dados
5. Testes
6. Frontend
7. Revisão
8. Documentação

---

# Stack do Projeto

## Backend

* Java
* Spring Boot

## Banco de Dados

* PostgreSQL

## Versionamento

* Git
* GitHub

## API Testing

* Postman

## Banco Visual

* DBeaver

## Frontend

[ DEFINIR ]
Ex:

* HTML/CSS/JS
* React

## Deploy Futuro

[ DEFINIR ]

---

# Estrutura Inicial do Projeto

```txt
cantinmoci/
│
├── backend/
├── frontend/
├── docs/
├── database/
└── README.md
```

---

# Arquitetura Backend

Estrutura esperada:

```txt
backend/
│
├── controllers/
├── services/
├── repositories/
├── models/
├── dtos/
├── config/
└── exceptions/
```

---

# Módulos do Sistema

## Módulo 1 - Autenticação

Status: [ NÃO INICIADO ]

Funcionalidades:

* login
* logout
* autenticação
* permissões
* usuários admin
* operadores

Observações:
[ EDITAR ]

---

## Módulo 2 - Produtos

Status: [ NÃO INICIADO ]

Funcionalidades:

* cadastrar produto
* editar produto
* remover produto
* listar produtos
* controlar estoque

Campos esperados:

* id
* nome
* preço
* quantidade
* categoria
* descrição

Observações:
[ EDITAR ]

---

## Módulo 3 - Estoque

Status: [ NÃO INICIADO ]

Funcionalidades:

* entrada de estoque
* saída automática em vendas
* alertas de estoque baixo

Observações:
[ EDITAR ]

---

## Módulo 4 - Vendas

Status: [ NÃO INICIADO ]

Funcionalidades:

* registrar venda
* múltiplos produtos
* cálculo automático
* histórico de vendas
* fechamento de caixa

Observações:
[ EDITAR ]

---

## Módulo 5 - Eventos

Status: [ NÃO INICIADO ]

Funcionalidades:

* cadastrar eventos
* vincular vendas ao evento
* relatórios por evento

Observações:
[ EDITAR ]

---

# Modelagem Inicial de Entidades

## Usuário

Campos:

* id
* nome
* email
* senha
* cargo

Relacionamentos:

* usuário pode realizar várias vendas

---

## Produto

Campos:

* id
* nome
* preço
* quantidadeEstoque

Relacionamentos:

* produto pode existir em várias vendas

---

## Venda

Campos:

* id
* valorTotal
* dataVenda

Relacionamentos:

* venda pertence a um usuário
* venda possui vários produtos

---

## ItemVenda

Campos:

* id
* venda_id
* produto_id
* quantidade

Relacionamentos:

* conecta vendas e produtos

---

# Padrões de Desenvolvimento

## Backend

* Utilizar arquitetura em camadas
* Separar responsabilidades
* Evitar lógica no controller
* Priorizar services para regras de negócio

---

## Banco de Dados

* Utilizar PostgreSQL
* Utilizar relacionamentos corretamente
* Evitar duplicação de dados

---

## Git/GitHub

Commits devem ser pequenos e organizados.

Exemplos:

* feat: criação da entidade Produto
* fix: correção cálculo estoque
* refactor: melhoria estrutura vendas

---

# Roadmap Inicial

## Fase 1

* Configurar backend
* Configurar PostgreSQL
* Criar conexão com banco
* Subir primeira API

---

## Fase 2

* Criar entidade Produto
* Criar CRUD Produto
* Testar no Postman

---

## Fase 3

* Criar autenticação
* Criar usuários

---

## Fase 4

* Criar sistema de vendas

---

## Fase 5

* Criar frontend inicial

---

# Regras Pessoais de Aprendizado

* Entender cada etapa antes de avançar
* Ler código gerado pela IA
* Testar tudo manualmente
* Fazer perguntas sobre cada camada
* Priorizar consistência sobre velocidade
* Não comparar evolução com outros desenvolvedores

---

# Ideias Futuras

[ EDITAR ]

Exemplos:

* dashboard administrativo
* PWA
* QR Code
* integração pagamentos
* relatórios PDF
* sistema multi-eventos
* app mobile

---

# Observações Gerais

[ EDITAR ]
