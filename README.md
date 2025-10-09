# Sistema de Gestão de Acessos Temporários

### Tecnologias utilizadas: PostgreSQL, Java 25 com Spring boot e Vue.js 3

# Execução do projeto

## Requisitos

- Possuir o docker instalado, para mais informações: [https://docs.docker.com/engine/install/](https://docs.docker.com/engine/install/)

Com o docker instalado basta entrar na raíz do projeto, onde encontra-se o arquivo "docker-compose.yml" e digitar o seguinte comando no terminal:

docker-compose up

Após aguardar a criação dos containers, acesse http://localhost:5173, onde será possível navegar pelo o sistema.

Para navegar nas funções de ADMIN do sistema, faça login com o usuário pré configurado chamado admin, email: admin@admin, senha: 1234. 
Nesse projeto de primeiro momento optei por essa regra de negócio não ser possível cadastrar admins diretamente, apenas usuários do tipo USER.

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







