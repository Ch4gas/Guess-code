# GuessCode RMI

Projeto em Java com RMI para um jogo de adivinhar uma sequencia secreta de 4 numeros entre dois jogadores.

## Regras implementadas

- Dois jogadores se conectam ao servidor.
- Cada jogador informa uma sequencia secreta com 4 digitos.
- Os turnos sao alternados.
- A cada palpite, o servidor informa:
  - numeros corretos na posicao certa;
  - numeros corretos na posicao errada;
  - numeros incorretos.
- Vence quem acertar primeiro a sequencia do adversario.

## Estrutura

```text
src/
  guesscode/
    api/
      GuessCodeService.java
    client/
      GuessCodeClient.java
    model/
      GameSnapshot.java
      GuessCodeException.java
      GuessResult.java
    server/
      GuessCodeServer.java
      GuessCodeServiceImpl.java
```

## Como compilar no PowerShell

Execute na pasta do projeto:

```powershell
New-Item -ItemType Directory -Force out
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src\*.java).FullName
```

## Como executar

Primeiro abra um terminal para o servidor:

```powershell
java -cp out guesscode.server.GuessCodeServer
```

Depois abra dois outros terminais, um para cada jogador:

```powershell
java -cp out guesscode.client.GuessCodeClient
```

Se o servidor estiver em outro computador da rede, execute o cliente informando o IP:

```powershell
java -cp out guesscode.client.GuessCodeClient 192.168.0.10
```

## Observacao

As sequencias aceitam digitos repetidos, por exemplo `1123`. O calculo das dicas trata repeticoes corretamente.
