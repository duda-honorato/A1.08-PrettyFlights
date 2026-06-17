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

RF11: O sistema deve alocar automaticamente uma aeronave a um portão compatível em menos de 2 segundos após a solicitação, considerando tamanho da aeronave, horários e capacidade de fluxo.

RNF11: O algoritmo de alocação deve ter 99,5% de disponibilidade durante o horário de pico (6h–22h) e registrar toda mudança de alocação em log imutável para auditoria.

Descrição completa: Para otimizar a operação aeroportuária e evitar gargalos, o PrettyFlights possui um módulo de alocação inteligente de portões (gates). O algoritmo distribui aeronaves nos portões disponíveis considerando três fatores principais: tamanho da aeronave (compatibilidade física), horário de pouso/decolagem (conflito de horários) e fluxo de passageiros (evitar superlotação em áreas de espera). Sempre que uma nova aeronave solicita alocação, o sistema deve calcular o portão mais adequado em tempo real, reservá-lo e notificar as equipes de solo e embarque.

### 2) Implementação do Requisito

src/main/java/com/prettyflights/
├── model/
│   ├── enums/
│   │   └── AircraftSize.java
│   ├── Aircraft.java
│   ├── Gate.java
│   └── Flight.java
├── service/
│   ├── GateAllocationService.java
│   └── GroundCrewService.java
├── repository/
│   ├── GateRepository.java
│   └── AuditRepository.java
└── controller/
    └── GateController.java
    
<Classes Implementadas>

1. AircraftSize (Enum)
2. Aircraft.java
3. Gate.java
4. GateAllocationService.java
   
### 3) Codificação dos Testes

> *Popular a seguinte tabela com todos os casos de teste implementados (unitários e de integração):*

| Tipo de Teste | Classe de Teste | Cenário de Teste (Método) | Técnicas Empregadas | Comentários da Dupla sobre a Implementação |
| --- | --- | --- | --- | --- |
| Unitário |  GateTest | testGateCompatibility_WideBodyOnNarrowGate_ShouldReturnFalse() | Análise de Valor Limite - Teste com valores extremos de tamanho de aeronave (Wide Body vs Narrow Gate) | Verificamos a incompatibilidade máxima entre tamanhos diferentes. O método isCompatibleWith() deve retornar false para qualquer aeronave de tamanho superior ao suportado pelo portão. |
| Integração |  GateAllocationIT | testDatabaseTransaction_AfterAllocation_ShouldUpdateThreeTables() | Tabela de Transição de Estados - Estados: INITIAL → RESERVED → ASSIGNED → AUDITED | Validamos a transição de estados: flight sem alocação → reserva do portão → atribuição → registro em auditoria. Verifica consistência transacional. |
| Sistema/E2E |  GateAllocationE2ETest | testNoAvailableGate_ShouldReturnConflict() | Análise de Valor Limite - Zero portões disponíveis (limite inferior) | Verifica comportamento quando todos os portões estão ocupados. Deve retornar conflito HTTP 409 com mensagem adequada. |
| Aceitação/UAT |  GateAllocationUAT | testUAT_RealOperationalDay_ShouldMeetANACStandards() | Particionamento de Equivalência - Cenário com 15 voos em horário de pico | Simula um dia operacional real no aeroporto com 15 voos, validando: nenhum Wide Body em Narrow Gate, tempos de resposta, e conformidade com normas ANAC. |

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
| X | 4 (Uso Específico) | Apresentar os casos de uso específico. |

### Registro de Prompts

Prompt 1: "Preciso implementar um sistema de alocação de portões em Java seguindo o modelo V de testes. Pode me ajudar com a estrutura de classes e os testes unitários?"
- Ferramenta: ChatGPT 4.0
- Data: 10/06/2024

Prompt 2: "Como implementar testes de integração para validar comunicação entre microsserviços usando RabbitMQ em Java?"
- Ferramenta: ChatGPT 4.0
- Data: 12/06/2024

Prompt 3: "Gere uma tabela com técnicas de teste (Particionamento de Equivalência, Análise de Valor Limite, Tabela de Transição de Estados) para os cenários do sistema de alocação de portões."
- Ferramenta: ChatGPT 4.0
- Data: 13/06/2024

Prompt 4: "Como simular um dia operacional real com 15 voos para teste de aceitação (UAT)?"
- Ferramenta: ChatGPT 4.0
- Data: 14/06/2024

### Validação Humana

Correção de nomenclatura: A IA inicialmente nomeou as classes de teste como GateUnitTest e GateAllocationIntegrationTest. Nós renomeamos para GateTest e GateAllocationIT para seguir as convenções do Maven Surefire/Failsafe.

Lógica de negócio específica: A IA sugeriu uma lógica de priorização baseada apenas no tamanho do portão. Nós ajustamos para considerar também a capacidade de fluxo de passageiros e horários de pico, alinhando com os requisitos reais da PrettyFlights.
