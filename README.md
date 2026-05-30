# Shortenator
**O Shortenator é um sistema encurtador de URLs que cria um link de acesso curto e o usa para redirecionar para a URL original.
Caso precise encurtar URLs para compartilhar links mais curtos, o Shortenator é a ferramenta para isso.**

[![PROD CI](https://github.com/C-Viana/shortenator-backend/actions/workflows/release-workflow.yml/badge.svg)](https://github.com/C-Viana/shortenator-backend/actions/workflows/release-workflow.yml)

## TECNOLOGIAS
- **Backend**: Spring Boot 4 e Java 25
- **Segurança**: Autenticação JWT
- **Banco de dados**: PostgreSQL 18
- **Cache**: Redis 8
- **Infraestrutura**: Docker e Docker Compose

## SOBRE O PROJETO
1. **Serviços**:
<br/>*TokenService*: criação e disponibilização de JWT para autenticação de usuários
<br/>*UserService*: criação e manipulação de usuários
<br/>*UrlService*: criação e manipulação de URLs encurtadas
2. **Entidades**:
<br/>*User*: entidade para cadastro de usuários da aplicação
<br/>*Url*: entidade para criação e redirecionamento de uma URL encurtada para a URL original
<br/>*UrlAccessLog*: entidade que persiste os dados de uso de uma URL, contabilizando quantidade de acessos e plataforma de operação
3. **Operação**:
<br/>*Encurtamento*: armazena a URL original e a relaciona a uma URL encurtada, individualizada através de um código gerado por Base62
<br/>*Redirecionamento*: consulta o código de Base62 da URL encurtada no cache. Caso ela não seja encontrada, busca então na base de dados, enviando-a ao cache e concluíndo com o redirecionamento


## Estrutura de projeto
```
com.cviana.app
├── config/
│   ├── SecurityConfig.java
│   └── RedisConfig.java
├── user/
│   ├── User.java
│   ├── UserRepository.java
│   ├── UserService.java
│   ├── UserController.java
│   └── dto/
│       ├── UserRequestDto.java
│       ├── UserResponseDto.java
│       └── CredentialsDto.java
├── url/
│   ├── Url.java
│   ├── UrlRepository.java
│   ├── UrlService.java
│   ├── UrlController.java
│   ├── dto/
│   │   ├── UrlRequestDto.java
│   │   └── UrlResponseDto.java
│   │── metrics/
│   │   ├ UrlAccessLog.java
│   │   ├ AccessLogRepository.java
│   │   └ dto/
│   │       └ MetricsResponseDto.java
│   └── mail/
│       └ GmailSender.java
├── auth/
│   ├── TokenService.java
│   ├── JwtAuthFilter.java
│   ├── AuthController.java
│   └── dto/
│       └── TokenDto.java
└── shared/
    ├── exception/
    ├── files/
	│     ├ CsvFile.java
	│     ├ ExcelFile.java
	│     ├ PdfFile.java
	│     ├ TextFile.java
	│	  └ FileCreator.java
    └── util/
	      ├ Base62Encoder.java
		  └ DeviceTypeResolver.java
```

## FUNCIONAMENTO
A execução deste projeto não se dá sem a integração de todos os componentes de seu ambiente.
Para tal, é necessário utilizar a infraestrutura conforme definida no arquivo docker-compose

### PRÉ-REQUISITOS
- Docker & Docker Compose

### VARIÁVEIS DE AMBIENTE
Para execução local, será necessário garantir a definição das variáveis de ambiente conforme a nomenclatura abaixo.
Como recomendação, crie um arquivo _.env_  na raíz do projeto com a atribuição dos valores.
- DB_HOST
- DB_PORT
- DB_NAME
- DB_USERNAME
- DB_PASSWORD
- REDIS_HOST
- REDIS_USERNAME
- REDIS_PASSWORD
- JWT_SECRET
- EMAIL_USERNAME
- EMAIL_PASSWORD

### ESTRUTURA DO DOCKER-COMPOSE
1. PostgreSQL
<br/>**postgres_db**: fará a inicialização do banco de dados
2. Redis
<br/>**redis_cache**: serviço de cache para consulta das URLs recém utilizadas
3. Aplicação backend
<br/>**backend**: aplicação Spring Boot para disponibilização dos serviços de encurtamento de URLs

### EXECUÇÃO LOCAL
1. Prepare os arquivos _.env_ ou configure as variáveis de sistema conforme a nomenclatura apresentada anteriormente.
2. Execute o docker-compose ``docker-compose up``

### FLUXO DE CONSULTA DE URLs
```
      [Redis Cache]      |    [shortenator-backend]    |      [PostgreSQL]       |
                         |              ↓              |                         |
                         |  Solicita redirecionamento  |                         |
                         |              ↓              |                         |
          ↓         ←    |      ←     [GET]            |                         |
                         |                             |                         |
Existe no cache?         |                             |                         |
        [NÃO]       →    |               →             |    →   URL existe?  →   |    →    [NÃO]    →    STATUS 404
                         |                             |             ↓           |
        [SIM]       →    |       Registra métricas     |    ←      [SIM]         |
                         |               ↓             |                         |
                         |        Salva no cache       |                         |
                         |               ↓             |                         |
                         |        Redireciona URL      |                         |
```

### ENDPOINTS

```
| Método | Rota                      | Autenticação | Descrição                             |
|--------|---------------------------|:------------:|---------------------------------------|
| POST   | /api/v1/users/signup      | Não          | Cadastro de usuário                   |
| POST   | /api/auth/signin          | Não          | Autenticação                          |
| POST   | /api/auth/logout          | Sim          | Cessar acesso                         |
| POST   | /api/v1/urls/shorten      | Sim          | Encurtar URL                          |
| GET    | /r/{code}                 | Não          | Redirecionar                          |
| GET    | /api/v1/urls              | Sim          | Listar URLs                           |
| GET    | /api/v1/urls/{id}/metrics | Sim          | Métricas de acesso                    |
| GET    | /api/v1/urls/export       | Sim          | Exportar CSV, PDF, TEXTO OU EXCEL     |
| DELETE | /api/v1/urls/{id}         | Sim          | Remover URL                           |
| POST   | /api/v1/urls/share        | Sim          | Envia uma planilha com todas as URLs  |
```

### LICENÇA
    Feito com ☕ e persistência por Carlos Eduardo de Souza Viana
    [LinkedIn](https://www.linkedin.com/in/carlos-eds-viana)
