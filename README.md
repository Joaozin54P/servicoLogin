# servicoLogin

Cadastro e login com **JWT** e perfis `CLIENTE`, `COZINHA` e `ADMIN`. Banco **H2** (em memória), porta **8085**.

## Os dois módulos

```
servicoLogin/
├── pom.xml          projeto (lista os 2 módulos)
├── domain/          REGRAS DE NEGÓCIO: Java puro, nenhum import de Spring
│   └── entity/ (Usuario, Perfil, Sessao)   repository/ (interface)   security/ (interfaces)
│       service/AuthService   exception/
└── spring/          tudo que usa o Spring Framework
    └── controller/ (API REST + DTOs)   repository/ (JPA + H2)   security/ (JWT, BCrypt)   config/
```

A ideia: o `domain` define **o que** precisa existir (ex: a interface `UsuarioRepository`, `SenhaCodificador`,
`TokenEmissor`) e o módulo `spring` define **como** (JPA/H2, BCrypt, JWT). O `spring` depende do `domain`,
nunca o contrário. Por isso o `AuthService` é testado sem Spring nem banco (`domain/src/test`).

## Abrir na IDE (IntelliJ)

1. `File > Open` e escolha o arquivo **`servicoLogin/pom.xml`** → *Open as Project* (*Trust Project*).
2. Aba **Maven** → *Reload All Maven Projects*. Devem aparecer `servico-login-domain` e `servico-login-spring`.
3. Se o `domain` aparecer como pasta comum: botão direito em `domain/pom.xml` → **Add as Maven Project**.
4. JDK do projeto: **21**.

## Rodar

- Pela IDE: abra `spring/.../ServicoLoginApplication` e clique em Run.
- Pelo terminal:

  ```
  mvn clean install
  cd spring
  mvn spring-boot:run
  ```

- Console do H2: <http://localhost:8085/h2-console>  (JDBC URL `jdbc:h2:mem:logindb`, usuário `sa`, senha vazia)
- O banco é em memória: ao reiniciar os usuários cadastrados somem (o admin e a cozinha são recriados sozinhos).

Para testar, abra o **`requests.http`** no IntelliJ e rode as requisições na ordem.

## Usuários criados automaticamente

| Perfil | Email | Senha |
| --- | --- | --- |
| ADMIN | <admin@lanchonete.com> | admin123 |
| COZINHA | <cozinha@lanchonete.com> | cozinha123 |

(dados em `spring/src/main/resources/application.yaml`, em `app.admin` e `app.cozinha`; troque as senhas)

## Rotas

| Rota | Acesso | Corpo |
| --- | --- | --- |
| `POST /auth/registrar` | público (sempre cria `CLIENTE`) | `{"nome","email","senha"}` |
| `POST /auth/login` | público | `{"email","senha"}` → `{"token","nome","perfil"}` |
| `POST /auth/usuarios` | só `ADMIN` | `{"nome","email","senha","perfil"}` |

Erros: `{"erro": "mensagem"}` com 400 (dados inválidos / email repetido), 401 (login inválido ou sem token) e 403 (sem permissão).

## O token (para os outros serviços)

O JWT vale 2 horas e carrega: `sub` = email, `role` = `CLIENTE` | `COZINHA` | `ADMIN`, `nome`.
Os outros microserviços só precisam do **mesmo `app.jwt.secret`** (HS256) para validar e ler o `role`
(no Spring, `hasRole("ADMIN")` espera a authority `ROLE_ADMIN`).
O front pode ligar o CORS: já estão liberados `localhost:3000`, `4200` e `5173` (`app.cors.origins`).

## Testes

- `domain` (sem Spring, sem banco): `cd domain && mvn test`: cadastro, email repetido, login, perfis.
- `spring`: `ServicoLoginApplicationTests` só verifica se o contexto sobe (usa o H2, não precisa de Docker).
