# Sistema de Gestão de Acessos Temporários - AVMB

### Tecnologias utilizadas: PostgreSQL, Java 25 com Spring boot e Vue.js 3

# Execução do projeto

## Requisitos

- Possuir o docker instalado, para mais informações: [https://docs.docker.com/engine/install/](https://docs.docker.com/engine/install/)

Com o Docker instalado, copie a configuração local e substitua os valores fictícios:

```bash
cp .env.example .env
docker compose up --build --wait
```

Após aguardar a criação dos containers, acesse http://localhost:5173, onde será possível navegar pelo o sistema.

O Compose base executa os artefatos otimizados no perfil `demo`, que cria o administrador demonstrativo usando `ADMIN_NAME`, `ADMIN_EMAIL` e `ADMIN_PASSWORD` definidos no seu `.env`. Apenas o Nginx fica exposto; backend e PostgreSQL permanecem na rede interna.

Para desenvolver com recarga automática e portas do backend e banco expostas localmente:

```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build --wait
```

Os testes ficam separados dos serviços normais:

```bash
docker compose --profile test run --rm frontend-unit
docker compose --profile test run --rm e2e
```

Em produção, use `SPRING_PROFILES_ACTIVE=prod` e forneça `JWT_SECRET` e as variáveis `SPRING_DATASOURCE_*` pelo ambiente de implantação. O perfil de produção não cria administrador automaticamente; essa conta deve ser provisionada por um processo administrativo separado.

## Planejamento do projeto

Primeiramente ao planejar esse projeto eu li e reli os requisitos, montei o modelo ER que mais fazia sentido para mim no presente momento, e trabalhei em cima dele.
Começando pelo o backend as entidades, repositorios e os controllers iniciais, depois foi a parte do login e register junto com a parte de segurança, aí criei a parte do frontend nesse enfoque e logo após os restantes requisitos garantindo que houvesse autenticação nas consultas.

# Modelo ER do banco

Conforme mostrei na imagem, o modelo ficou daquele jeito, onde um usuário pode ter vários acessos, e um acesso apenas um usuário vinculado. Explicando mais sobre os campos de cada entidade:

Usuario possui o id para se identificar como chave primária, nome, email e senha conforme foi solicitado nos requisitos do sistema, role para que seja possível identificar seu nível de acesso e status se ele foi ou não aprovado.
Acesso segue nos mesmos moldes, nomeRecurso representando o acesso ao recurso temporário que o admin lhe concedeu acesso, horaPermissao e horaExpiracao referem-se cada um as datas iniciais e finais dos respectivos acessos, revogado é utilizado para verificar se o acesso está ou não revogado e usuarioId para identificar a quem pertence o acesso.

# Arquitetura do backend

Optei por seguir pela a arquitetura que acredito ser a tradicional que é a em camadas (Controller -> Service -> Repository), junto com a inclusão da JPA/Hibernate para a persistência de dados e a implementação do JWT. No geral seguindo os requisitos para atender o que foi solicitado no desafio.

## Estrutura do backend final

```
src/
 ├─ main/java/com/example/demo/
 │   ├─ config/           → Filtros de segurança, configuração do CORS
 │   ├─ controller/       → Endpoints REST 
 │   ├─ service/          → Lógica do negócio
 │   ├─ model/            → Entidades JPA (Usuario, Acesso)
 │   ├─ repository/       → Interfaces JPA 
 │   ├─ security/         → Configurações de JWT
 │   ├─ dto/              → DTOs para requests/responses
 │   └─ task/             → Auto execuçao de tarefas automatico (verifica possiveis acessos expirados)
 └─ resources/
     └─ application.properties (configurações gerais de conexao com banco além das variaveis globais do JWT)
```

# Arquitetura do frontend

Como foi minha primeira vez mexendo com o Vue acabei estruturando da seguinte maneira para que atendesse aos requisitos, acredito que no geral talvez algo padrão, como views(páginas do sistema), router(para mapear as páginas), components(navbar), store(interação com a api).

## Estrutura do frontend final

```
src/
 ├─ assets/             → Nada de especial, apenas mantive após criar o projeto na linha de comando
 ├─ components/         → Navbar reutilizável após realizar o login
 ├─ store/              → Interação com a api, coleta e envio de dados
 ├─ views/              → Telas principais do sistema (login, register, home, etc)
 ├─ router/             → Rotas mapeadas e protegidas por cada tipo de usuário
 ├─ utils/              → Funções compartilhadas entre as views (formatarData, calcularTempoRestante)
 └─ main.js
```









