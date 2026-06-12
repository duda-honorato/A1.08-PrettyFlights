# PrettyFlights [ES2] - Projeto Modelo para a Atividade A1.08

## Contexto

Este repositório tem fins exclusivamente didáticos, servindo como ferramenta de apoio ao desenvolvimento de competências em Engenharia de Software II ao longo dos conteúdos do plano de ensino da disciplina.

| Item | Descrição |
| --- | --- |
| **Instituição** | Universidade do Vale do Itajaí (Univali) |
| **Curso** | Ciência da Computação |
| **Disciplina** | Engenharia de Software II |
| **Professor** | Roger Anderson Schmidt |

## Regras de Negócio do Projeto Modelo

O projeto modelo implementa a Política de Cancelamento de Voo e Cálculo de Reembolso, que envolve lógica condicional baseada no tempo restante para o voo.

No contexto da plataforma PrettyFlights, um importante cenário de integração é o processo de compra e reserva de passagem. Esse fluxo exige que o componente de negócio (ServicoReservaVoo) interaja com duas dependências simuladas na forma abaixo:

- Banco de Dados (Persistência): Para verificar se a reserva foi salva corretamente e se os assentos disponíveis no voo foram decrementados.

- Gateway de Pagamentos (API): Para simular a integração com um serviço externo de cobrança.

## Arquitetura (Stack de Componentes)

### [JUnit](https://junit.org/)

Framework de testes unitários.

### [Spring Boot Test](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html)

Framework de testes de integração.

### [H2 Database](http://h2database.com/html/main.html)

Banco de dados em memória (*in-memory database*), que dispensa a necessidade de instalação de um banco local como PostgreSQL ou MySQL, reduzindo a complexidade na configuração do ambiente de testes.

### [Maven](https://maven.apache.org/)

Ferramenta de empacotamento (build) do ecossistema Java.

## Comandos para Execução dos Testes

1) Inicializar o projeto:
   ```
   mvn clean install -DskipTests
   ```

2) Rodar **todos** os testes (Unitários e de Integração):
   ```
   mvn test
   ```

3) Rodar apenas **Testes Unitários** (sufixo `Test`):

   Execução mais performática, pois não inicia o contexto do Spring Boot (isolamento das dependências).

   ```
   mvn test -Dtest="*Test"
   ```
4) Rodar apenas **Testes de Integração** (sufixo `IT`):
   ```
   mvn test -Dtest="*IT"
   ```

## Incremento Semanal A1.08 (CADA DUPLA DEVE RESPONDER AOS 4 ITENS)

### 1) Identificação do Requisito

> *Colar aqui o texto do requisito escolhido pela dupla (copiado do [board do Miro da A1.07](https://miro.com/app/board/uXjVHNLB3x8=/?share_link_id=916723536145)).*

### 2) Implementação do Requisito

> *Apresentar as classes implementadas pela dupla (solução de design) para atender ao requisito alvo dos testes automatizados.*

### 3) Codificação dos Testes

> *Popular a seguinte tabela com todos os casos de teste implementados (unitários e de integração):*

| Tipo de Teste | Classe de Teste | Cenário de Teste (Método) | Técnicas Empregadas | Comentários da Dupla sobre a Implementação |
| --- | --- | --- | --- | --- |
| Unitário / Integração |  *Preencher* | *Preencher* | *Identificar __Particionamento de Equivalência__, __Análise de Valor Limite__ ou __Tabela de Transição de Estados__* | *Preencher* |

### 4) Declaração de Uso da IA

Esta seção deve ser obrigatoriamente preenchida em todas as atividades práticas 
entregues na disciplina de Engenharia de Software II.

### Nível de Utilização

> Marque a opção que melhor descreve o uso da IA generativa neste trabalho: 

| Seleção | Nível | Descrição |
| -- | --- | --- |
|  | 0 | Não usei IA generativa |
|  | 1 (Assistência) | Usei apenas para correção gramatical, tradução ou formatação de referências. |
|  | 2 (Co-piloto) | Usei para gerar techos de código *boilerplate*, scripts de CI/CD ou sugestão de casos de teste. |
|  | 3 (Consultoria) | Usei para debater decisões arquiteturais ou entender padrões de projeto específicos. |
|  | 4 (Uso Específico) | Apresentar os casos de uso específico. |

### Registro de Prompts

> Cole aqui os principais prompts utilizados, registrando a devida ferramenta e 
versão correspondentes (Exemplos: Gemini, ChatGPT, Claude, GitHub Copilot):

Exemplo: *"Pedi ao Claude 3.5 para comparar o uso de Observer e Pub-Sub em 
uma arquitetura de microsserviços orientada a eventos."*

### Validação Humana

> Descreva  como  você  validou  a  saída  da  IA.  O  que  você  precisou  corrigir  ou 
adaptar para que o resultado fizesse sentido no contexto da atividade? 

Exemplo:  *"A IA sugeriu um script do GitHub Actions que usava uma versão obsoleta do Node.js. Tive que atualizar a  versão e configurar as Secret Keys manualmente no repositório."*