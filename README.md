# Territory Predictive Insights

Sistema de monitoramento territorial para risco de alagamento (recorte: clima local), desenvolvido para a Global Solution 2º/2026 de OOP. O sistema lê sensores simulados, grava o histórico em banco de dados, gera previsões e emite alertas explicados.

## Autoria
- Gustavo Lemos Diogenes — RM: 565579
- Pedro Paulo Barbosa Ross - RM: 563038
- Repositório: https://github.com/Gustavo-Diogenes/territory-predictive-insights

## Público beneficiado e decisão apoiada
- Quem sofre o problema: moradores de áreas sujeitas a alagamento (ruas, bueiros e pontos baixos) e quem depende dessas vias.
- Quem usa o sistema: associações de bairro, equipes de manutenção e defesa civil.
- Decisão que o sistema apoia: onde e quando limpar um bueiro, posicionar uma equipe ou avisar moradores *antes* da chuva intensa. O sistema indica o risco; a decisão continua sendo das pessoas.

## Como funciona
1. Os sensores de um ponto vulnerável são lidos e cada medição válida é gravada no banco.
2. Duas estratégias de previsão rodam sobre o histórico do banco:
   - Média móvel: média das últimas 5 leituras (mínimo de 5 leituras).
   - Comparação com o período anterior: projeta a variação entre a média das últimas 3 leituras e a das 3 anteriores (mínimo de 6 leituras).
3. Cada previsão é comparada com os limiares do tipo de sensor. Níveis: ATENÇÃO, ALERTA e CRÍTICO.
4. O alerta explica o porquê: qual sensor, valor previsto e limite ultrapassado. Alertas ficam gravados no banco.

Limiares iniciais (valores definidos pelo autor, sem fonte oficial; podem ser alterados no menu):

| Sensor | Atenção | Alerta | Crítico |
|---|---|---|---|
| Pluviômetro (mm/h) | 20 | 40 | 60 |
| Nível do bueiro (cm) | 40 | 60 | 80 |
| Umidade do solo (%) | 60 | 75 | 90 |

Todas as leituras são simuladas (`Random`, em faixas realistas). A região e a população do menu são dados fictícios: não há dados pessoais.

## Tecnologias
Java 17+ (o projeto compila com `release 17`), Maven, JDBC com H2 (banco em arquivo), JUnit 5 e Mockito.

## Como executar
Requisitos: JDK 17 ou superior e Maven 3.9 ou superior. Não é preciso instalar nem configurar banco de dados.

```
mvn test          # roda os testes
mvn exec:java     # abre o menu
```

O banco H2 é criado sozinho na pasta `dados/` (tabelas `medicao` e `alerta`) e os dados permanecem ao reiniciar o programa. Para começar do zero, apague a pasta `dados/`.

## Estrutura
```
src/main/java/
  SistemaMonitoramento.java   menu principal (Scanner)
  dominio/     ComponenteTerritorial, PontoVulneravel, Sensor, SensorSimulado e 3 sensores,
               DadosTerritorio, Limiares, Medicao, Alerta, NivelAlerta,
               EstrategiaPrevisao, PrevisaoMediaMovel, PrevisaoComparacao, TipoPrevisao
  excecao/     DadosInvalidosException, FonteIndisponivelException
  persistencia/ ConexaoBanco, MedicaoDAO, AlertaDAO
  servico/     ServicoMonitoramento, FabricaPrevisao, ResultadoLeitura, RelatorioPrevisao
src/test/java/ testes unitários e de integração
```

## Decisões do projeto
- **JDBC com DAO** em vez de Spring/JPA: menos "mágica" e mais fácil de explicar. Todo SQL usa `PreparedStatement` e `try-with-resources`.
- **H2 em arquivo**: roda na correção sem configuração e mantém o histórico entre execuções.
- **`Medicao` e `Alerta` imutáveis**, validados no construtor: um objeto inválido nunca existe.
- **Exceções checked** (`extends Exception`): o compilador obriga quem chama a tratar o erro; o menu captura tudo com mensagem clara e nenhum `catch` fica vazio.
- **Previsão simples** (média e comparação), como o enunciado orienta: o foco é estrutura, proteção, persistência e testes.

## Padrão criacional: Factory (`FabricaPrevisao`)
A `FabricaPrevisao` é o único lugar que conhece as classes concretas de previsão e seus valores padrão (janela 5 e período 3). O `main` pede as estratégias à fábrica, e o `ServicoMonitoramento` trabalha só com `EstrategiaPrevisao`, sem `if` por tipo nem `instanceof`. Escolhi Factory e não Singleton ou Builder porque o ponto de variação do sistema é *qual previsão criar*: adicionar uma nova estratégia exige mudar só a fábrica e o enum `TipoPrevisao`.

## Testes
- Testes no padrão AAA para `Medicao`, sensores, `PontoVulneravel`, `DadosTerritorio`, `Alerta`, previsões e fábrica.
- `assertThrows` valida as exceções customizadas em vários testes.
- `ServicoMonitoramentoTest` usa Mockito para simular os DAOs e testar a lógica de alerta sem banco.
- `MedicaoDAOTest` e `AlertaDAOTest` são testes de integração com H2 em memória.

## Reflexão crítica
Quando o sistema deve se calar? 
- Com menos leituras do que a estratégia exige (5 na média móvel, 6 na comparação) a previsão não é confiável: o sistema se recusa a prever, explica o motivo e não emite alerta. Ele também não gera leitura de um sensor com falha. Além disso, como as leituras desta versão são aleatórias, as previsões não têm valor preditivo real; servem para demonstrar a estrutura, não para decidir algo no mundo real.

O que os dados não representam? 
- Um sensor por ponto diz pouco sobre a rua inteira ou o bairro. Médias de leituras recentes ignoram previsão meteorológica, sazonalidade e obras. Os limiares foram definidos pelo autor e não por critério técnico oficial, e a população e a região são fictícias.

Por que o alerta apoia a decisão em vez de decidir?
- O sistema não conhece o contexto: um bueiro pode ter acabado de ser limpo, pode haver evento na região ou o sensor pode estar descalibrado. Por isso o alerta mostra sensor, valor previsto e limite estourado, para que uma pessoa avalie e decida se envia uma equipe ou avisa os moradores.

## Uso de IA
Declaração de uso de IA:
- Onde usei: utilizamos o assistente Claude (Anthropic) como apoio durante o desenvolvimento: planejamento da arquitetura e da ordem de implementação, auxilio para a criação do código-base das classes e dos testes e explicações sobre os conceitos (exceções, JDBC, padrão Factory, Mockito) e criação da documentação.
- Como revisei: Ajustamos as respostas do Claude para o prompt e pedimos explicações sobre trechoes que não entedemos, seja do arquivo da atividade ou da resposta enviada por ele.

## Divisão de tarefas
Projeto desenvolvido por Gustavo Lemos com tarefas de persistência, previsões, padrão criacional, menu e testes. e Pedro Paulo, com tarefas de domínio, exceções, serviço e documentação.