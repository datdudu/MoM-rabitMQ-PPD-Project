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