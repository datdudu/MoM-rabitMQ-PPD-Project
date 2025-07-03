# Projeto MOM - Sistema de Gerenciamento e Utilização de Comunicação por Mensagens

## 📋 Visão Geral do Projeto

Este projeto consiste na implementação de um Sistema de Gerenciamento e Utilização de Comunicação por Mensagens (MOM - Message-Oriented Middleware), utilizando **Java** e **RabbitMQ** como broker de mensagens. O objetivo principal é demonstrar a comunicação assíncrona entre diferentes aplicações (Broker e Clientes/Usuários) através de filas e tópicos de mensagens.

O sistema é dividido em duas aplicações principais:

1. **Broker Manager:** Uma aplicação de gerenciamento que permite ao administrador controlar usuários, filas e tópicos no RabbitMQ.
2. **User Application (Cliente):** Uma aplicação cliente que permite aos usuários se conectar ao sistema, enviar mensagens diretas para outros usuários, e enviar/receber mensagens via tópicos.

## ⚡ Funcionalidades Implementadas

### 🔧 Broker Manager

O Broker Manager oferece uma interface gráfica (GUI) para o administrador gerenciar o ambiente de mensagens:

- **Gerenciamento de Usuários:**
    - Adicionar novos usuários ao sistema
    - Remover usuários existentes
    - Verificação de duplicidade de nomes de usuário
    - Criação automática de uma fila pessoal (`user_<username>`) para cada novo usuário no RabbitMQ

- **Gerenciamento de Tópicos:**
    - Adicionar novos tópicos (exchanges do tipo `fanout`) para comunicação de "um para muitos"
    - Remover tópicos existentes
    - Listagem de todos os tópicos disponíveis no RabbitMQ

- **Gerenciamento de Filas Genéricas:**
    - Adicionar e remover filas que não estão diretamente associadas a usuários
    - Permite criação de canais de comunicação para propósitos específicos (ex: logs, tarefas, eventos)
    - Listagem de todas as filas (incluindo as de usuário e as genéricas) presentes no RabbitMQ

- **Monitoramento de Filas:**
    - Visualização da quantidade de mensagens pendentes em cada fila de usuário
    - Panorama do tráfego de mensagens em tempo real

### 👤 User Application (Cliente)

A aplicação cliente permite que os usuários interajam com o sistema de mensagens:

- **Login de Usuário:**
    - Conexão ao sistema com um nome de usuário existente (verificado no RabbitMQ)
    - Exibição do nome do usuário logado na interface

- **Comunicação Direta (Ponto a Ponto):**
    - Envio de mensagens diretas para outros usuários
    - Suporte a mensagens offline: mensagens aguardam em fila até que o destinatário se conecte

- **Comunicação por Tópicos (Publicação/Assinatura):**
    - Assinar tópicos de interesse para receber mensagens publicadas
    - Desassinar tópicos para parar de receber mensagens
    - Envio de mensagens para tópicos específicos, distribuídas para todos os assinantes
    - Atualização dinâmica da lista de tópicos disponíveis

- **Comunicação com Filas Genéricas:**
    - Envio de mensagens para qualquer fila genérica existente no RabbitMQ
    - Demonstra a flexibilidade do MOM para integração com outros componentes

- **Exibição de Mensagens:**
    - Interface de chat para exibir mensagens enviadas e recebidas
    - Formatação clara com timestamps e identificação de origem

## 🏗️ Arquitetura do Sistema

O sistema segue uma arquitetura cliente-servidor, onde o **RabbitMQ** atua como o broker central de mensagens.

- **RabbitMQ (Broker MOM):** Responsável por rotear e armazenar mensagens. Gerencia filas (para mensagens diretas e genéricas) e exchanges (para tópicos)
- **Broker Manager (Aplicação Java):** Interage com o RabbitMQ via API de gerenciamento e cliente Java para administrar o ambiente
- **User Application (Aplicação Java):** Conecta-se ao RabbitMQ para enviar e receber mensagens de filas pessoais e tópicos

### Diagrama Simplificado
```

+-----------------+       +-----------------+       +-----------------+
|                 |       |                 |       |                 |
|  User App (João)| <---> |                 | <---> |  User App (Maria)|
|                 |       |                 |       |                 |
+-----------------+       |   RabbitMQ      |       +-----------------+
                          |    (Broker)     |
+-----------------+       |                 |       +-----------------+
|                 |       |                 |       |                 |
|  User App (Pedro)| <---> |                 | <---> | Broker Manager  |
|                 |       |                 |       |                 |
+-----------------+       +-----------------+       +-----------------+
```

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java
- **Framework:** Spring Boot (para injeção de dependências e gerenciamento de componentes)
- **Interface Gráfica:** Swing (javax.swing)
- **Broker de Mensagens:** RabbitMQ
- **Cliente RabbitMQ:** `com.rabbitmq:amqp-client`
- **API de Gerenciamento RabbitMQ:** Utilização de requisições HTTP para interagir com a API REST do RabbitMQ

## 🚀 Como Executar o Projeto

### Pré-requisitos

1. **Java Development Kit (JDK) 17 ou superior:** Certifique-se de ter o JDK instalado e configurado
2. **Apache Maven:** Para gerenciar as dependências do projeto
3. **RabbitMQ Server:**
    - Instale o RabbitMQ: [https://www.rabbitmq.com/download.html](https://www.rabbitmq.com/download.html)
    - Certifique-se de que o plugin de gerenciamento esteja habilitado: `rabbitmq-plugins enable rabbitmq_management`
    - O RabbitMQ deve estar rodando e acessível em `localhost:5672` (porta padrão) e o painel de gerenciamento em `localhost:15672`

### Passos para Execução

1. **Clone o Repositório:**
   ```bash
   git clone git@github.com:datdudu/MoM-rabitMQ-PPD-Project.git
   cd MoM-rabitMQ-PPD-Project
   ```
2. **Compile o Projeto:**
   ```bash
   mvn clean install
   ```
3. **Execute o Broker Manager:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments="--app.type=broker"
   ```
   - A interface gráfica do Broker Manager será iniciada.
4. **Execute as Aplicações Cliente (Usuários):**
   - Abra um novo terminal para cada cliente que você deseja iniciar.
   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments="--app.type=client"
   ```
   - A interface gráfica do Cliente será iniciada. Você pode abrir múltiplas instâncias para simular vários usuários.

## 🧪 Testando as Funcionalidades

### No Broker Manager:
- Crie alguns usuários (ex: `joao`, `maria`, `pedro`). Observe que as filas `user_joao`, `user_maria`, `user_pedro` serão criadas no RabbitMQ.
- Crie alguns tópicos (ex: `esportes`, `noticias`).
- Crie algumas filas genéricas (ex: `logs_do_sistema`, `tarefas_pendentes`).

### Nas Aplicações Cliente:
- Faça login com os usuários criados (ex: `joao`).
- Assine tópicos (ex: `esportes`).
- Envie mensagens diretas para outros usuários (ex: `joao` envia para `maria`).
- Envie mensagens para tópicos (ex: `joao` envia para `esportes`).
- Envie mensagens para filas genéricas (ex: `joao` envia para `logs_do_sistema`).

### Observe a comunicação:
- Mensagens diretas devem aparecer apenas para o destinatário.
- Mensagens de tópico devem aparecer para todos os usuários que assinaram aquele tópico.
- Verifique o painel de gerenciamento do RabbitMQ (`localhost:15672`) para monitorar filas, exchanges e mensagens.

## 📚 Estrutura do Projeto
```
src/
├── main/
│ ├── java/
│ │ └── com/MoMPPD/demo/
│ │ ├── broker/ # Broker Manager
│ │ ├── user/ # User Application
│ │ ├── models/ # Modelos de dados
│ │ └── utils/ # Utilitários (RabbitMQ Connection)
│ └── resources/
│ └── application.properties
```

## 🤝 Contribuições

Sinta-se à vontade para explorar, testar e sugerir melhorias.

## 📄 Licença

Este projeto foi desenvolvido para fins acadêmicos como parte da disciplina de Programação Paralela e Distribuída.
