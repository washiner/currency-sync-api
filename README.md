# 💱 Currency Sync API

API REST para sincronização de cotações de moedas (BRL, USD, EUR, JPY) com notificações em tempo real via WebSocket e proteção contra abuso por Rate Limiting.

Projeto desenvolvido como prática da **Fase 7 — Nível Sênior** do estudo para o cargo de Analista de TI Sênior (SEPLAG/MT), cobrindo três competências exigidas pelo edital: comunicação em tempo real, controle de taxa de requisições e sincronização com sistemas externos.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.15-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)

---

## 📋 Sobre o projeto

O sistema mantém um cadastro de moedas e suas cotações **em relação ao Real (BRL)**. Um serviço de sincronização busca cotações reais em uma API externa (Frankfurter / Banco Central Europeu) e atualiza o banco de dados. Toda atualização é transmitida em tempo real, via WebSocket, para qualquer cliente conectado — sem necessidade de polling.

Por simplicidade de escopo (foco no aprendizado dos tópicos novos), o projeto não implementa autenticação. O Rate Limiting identifica o cliente pelo endereço IP da requisição.

## 🏗️ Arquitetura

```
Cliente HTTP                    Cliente WebSocket
     │                                  ▲
     ▼                                  │
┌─────────────┐                         │
│ RateLimit   │ (intercepta toda        │
│ Filter      │  requisição HTTP)       │
└─────┬───────┘                         │
      ▼                                 │
┌─────────────┐    ┌──────────────┐     │
│ Controller  │───▶│   Service    │     │
└─────────────┘    └──────┬───────┘     │
                           │             │
              ┌────────────┼─────────────┘
              ▼            ▼
      ┌──────────────┐  ┌──────────────────┐
      │  Repository  │  │  SimpMessaging   │
      │  (PostgreSQL)│  │  Template (/topic)│
      └──────────────┘  └──────────────────┘
              ▲
              │
      ┌──────────────┐
      │ Frankfurter  │ (API externa de
      │ Client       │  cotações - ECB)
      └──────────────┘
```

## ⚙️ Tecnologias e decisões técnicas

| Tecnologia | Uso no projeto | Por quê |
|---|---|---|
| **Java 21 + Spring Boot 3.5.15** | Base da aplicação | Stack principal de estudo |
| **PostgreSQL + Flyway** | Persistência | `ddl-auto=validate`: controle total e versionado do schema, sem alterações automáticas do Hibernate |
| **MapStruct** | Conversão Entity ↔ DTO | Evita boilerplate de mapeamento manual |
| **Spring WebSocket (STOMP)** | Notificações em tempo real | Permite ao servidor avisar os clientes sem que precisem perguntar (polling) |
| **WebClient** | Consumo da API externa | Cliente HTTP moderno do Spring, usado aqui em modo bloqueante (`.block()`) por simplicidade didática |
| **Rate Limiting customizado** | Proteção contra abuso | Implementado manualmente (Filter + `ConcurrentHashMap` + `AtomicInteger`) para fixar o algoritmo de Janela Fixa, sem depender de biblioteca pronta |
| **Docker Compose** | Ambiente de banco | PostgreSQL com health check (`pg_isready`) |
| **Springdoc OpenAPI** | Documentação | Swagger UI gerado a partir das anotações dos Controllers |

### Decisões de design relevantes

- **Cotação sempre em relação ao BRL**: cada moeda guarda um único valor (`1 unidade = X BRL`), evitando uma tabela combinatória de pares de conversão. Qualquer conversão entre duas moedas estrangeiras pode ser calculada a partir desses valores.
- **BRL não é sincronizado**: por ser a moeda de referência, seu `currentRate` permanece como valor de controle.
- **Cotação inicial = zero**: ao cadastrar uma moeda, ela nasce com `currentRate = 0`. O valor real só é preenchido após a primeira sincronização — isso torna visível, durante o teste manual, a diferença entre "antes" e "depois" da sincronização.
- **Rate Limiting por IP, sem autenticação**: decisão consciente de escopo, para manter o foco nos três tópicos novos da fase (WebSocket, Rate Limiting, sincronização externa), sem reconstruir a camada de autenticação já estudada em fase anterior.

## 🚀 Como rodar o projeto

### Pré-requisitos
- Java 21
- Docker e Docker Compose
- Maven (ou usar o wrapper `./mvnw` incluso)

### Passo a passo

```bash
# 1. Suba o banco de dados
docker compose up -d

# 2. Confirme que o container está saudável
docker ps

# 3. Rode a aplicação (via IDE ou linha de comando)
./mvnw spring-boot:run
```

A aplicação inicia em `http://localhost:8080`.

### Acessos úteis

| Recurso | URL |
|---|---|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| Página de teste do WebSocket | `http://localhost:8080/test-websocket.html` |

## 📡 Endpoints principais

### Moedas (`/v1/currencies`)

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/v1/currencies` | Lista todas as moedas cadastradas |
| `GET` | `/v1/currencies/{id}` | Busca uma moeda pelo ID |
| `POST` | `/v1/currencies` | Cadastra uma nova moeda (`code`, `name`) |
| `DELETE` | `/v1/currencies/{id}` | Remove uma moeda |

### Sincronização (`/v1/currencies/sync`)

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/v1/currencies/sync` | Dispara a busca de cotações reais na API externa e atualiza todas as moedas (exceto BRL) |

### WebSocket

| Endpoint | Tópico | Descrição |
|---|---|---|
| `/ws-currency` (SockJS) | `/topic/currency-updates` | Canal de notificação em tempo real, disparado após cada atualização de cotação |

## 🛡️ Rate Limiting

Implementação própria (sem biblioteca externa), usando o algoritmo de **Janela Fixa**:

- Limite: **10 requisições por minuto**, por endereço IP
- Acima do limite: resposta `429 Too Many Requests`
- Estruturas usadas: `ConcurrentHashMap` (uma ficha por IP) + `AtomicInteger` (contagem thread-safe) + bloco `synchronized` (consistência por IP)

## 🧪 Testes realizados

- CRUD completo validado via Swagger UI
- Sincronização validada com dados reais da API Frankfurter (ECB)
- WebSocket validado com cliente HTML/STOMP, recebendo notificações em tempo real sem polling
- Rate Limiting validado via script PowerShell disparando 11 requisições sequenciais (10× `200`, 1× `429`)

## 📚 Aprendizados desta fase

- Diferença entre comunicação request/response (HTTP tradicional) e comunicação pub/sub (WebSocket + STOMP)
- Consumo de API externa com `WebClient`, incluindo tratamento de falha isolado por item (uma moeda falhar não interrompe a sincronização das demais)
- Conceitos básicos de concorrência aplicados a um cenário real (`AtomicInteger`, `volatile`, `synchronized`, `ConcurrentHashMap`)
- Algoritmo de Janela Fixa para controle de taxa de requisições

---

**Autor:** Washiner — projeto de estudo para o cargo de Analista de TI Sênior (SEPLAG/MT)
