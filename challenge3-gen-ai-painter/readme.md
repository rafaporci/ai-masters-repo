# Challenge 4 Teams - AAUT2 e LNSCIA - Using Gen-AI and Conversational Agents to customize images in the style of famous painters

Sistema conversacional de descoberta de preferências artisticas dos utilizadores com o objetivo de gerar imagens aplicando-se o estilo preferido pelo mesmo.

## Estrutura do projeto

1. conversation-system/rasa: sistema conversacional totalmente implementado usando o RASA, contém o projeto com modelos de treino, DataSets e ficheiros de configuração.
2. image-generator: contém o código utilizado para exploração da solução para geração as imagens, notebooks de treino de cada pintor, bem como os códigos criados para conversão de imagens.
3. api: utilizado para a geração de imagens através do estilo de um pintor.

## Instruções para execução

### conversation-system/rasa

Pré-requisitos: 
- Python (pacote Rasa)

Com o RASA instalado, basta executar o comando "rasa shell" para interagir com o ChatBot via prompt ou "rasa run --enable-api --cors *" para rodar a API.

### image-generator

Pré-requisitos: 
- Jupyter ou similar (para a execução dos Notebooks)
- Python (pacotes numpy, keras and tensorflow)

O folder notebooks contém os notebooks base e as versões por pintor.
O ficheiro image-conversor.py contém o programa usado para converter conjuntos de imagens em ficheiros tfrecord utilizados para treino. As imagens ainda precisam ser enquadradas e redimensionadas já que o modelo necessita de imagens em 256x256.

### api

Pré-requisitos: 
- Python (pacotes flesk, flask-cors, keras and tensorflow)

Para executar a API, basta executar flask --app server run
A API estará em execução no endereço http://localhost:5000.

Modelos pré-treinados podem ser encontrados aqui https://drive.google.com/drive/folders/11kRChsyuXSmRMzbxBjzxBa0cwSGNR1Au?usp=drive_link.
