# Challenge 4 Teams - PPROG e ENGCIA - Network Discovery

Sistema pericial para descoberta de rotas de transporte baseado em regras logísticas.

## Estrutura do projeto

2. trab-drools: contém o projeto Drools com os módulos de descoberta de tradelanes e simulação de escolha de serviços premium

## Instruções para execução

### Drools

Pré-requisitos: 
- Java 14 (máximo)

O projeto Drools (/trab-drools) pode ser aberto no IntelliJ ou qualquer outra IDE Java. Basta executar e terá a API disponível no endereço http://localhost:4567.


## Módulos

### Módulo principal - Descoberta de tradelanes

Módulo que permite a alimentação de duas bases de conhecimento (uma em Drools, outra em Prolog) e a consequente descoberta de tradelanes a partir disso. Ambas possuem APIs que permitem:
- Integração com o front-end em Angular desenvolvido para o trabalho para fins de demonstração.
- Integração com sistemas logísticos que exibem as opções de entrega durante o processo de compra num e-commerce.

O módulo também conta com a funcionalidade de bloqueio de ponto logístico com o objetivo de reagir a eventos externos (guerras ou constrangimentos logísticos no geral), esse bloqueio pode ser feito:
- Drools via API POST /blockLogisticPoint:
{
    "logisticPointName": "Hong Kong",
    "reason" : "Logistics problems"
}
- Prolog via adição de fatos: add_bloqueio(PontoLogistico). ex.: add_bloqueio(hongKong).

Bases de conhecimento envolvidas nesse módulo podem ser atualizadas em:
- trab-prolog/BaseDeConhecimento.txt
- trab-drools/src/main/resources/org/engcia/rules.drl

### Módulo adicional - Simulador de serviços Premium

Módulo que permite a alimentação de uma base de conhecimento em Drools com evidências e fatores de certeza relativo a probabilidade de uma determinada compra ter um serviço Premium escolhido. O módulo possui uma API que permite a integração com o front-end em Angular desenvolvido para o trabalho para fins de demonstração e análise de viabilidade de implantação de serviços Premium numa determinada rota.

Base de conhecimento envolvida nesse módulo pode ser atualizada em:
- trab-drools/src/main/resources/org.engcia.fc/rules-fc.drl

