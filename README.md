# [프로젝트 이름]

> [MSA 기반 실시간 경매 플랫폼]

<br>

## 1. 프로젝트 개요

본 프로젝트는 대규모 트래픽과 실시간 동시 입찰 요청을 안정적으로 처리하는 것을 목표로 설계된 **마이크로서비스 아키텍처(MSA) 기반 경매 플랫폼**입니다.

주요 비즈니스 로직(입찰 요청, 처리)을 **비동기 메시지 큐(Kafka)**를 통해 분리하여 시스템의 안정성과 확장성을 확보했습니다. 또한 **SSE(Server-Sent Events)**를 통해 클라이언트에게 실시간으로 입찰 현황 및 결과를 전송합니다.

<br>

## 2. 시스템 아키텍처


[Image of System Architecture Diagram]

> 추후 추가 예정(아키텍처 이미지 부분)

### 서비스 구성

| Service | 역할 | 주요 기술 |
| :--- | :--- | :--- |
| **API Gateway** | 모든 클라이언트 요청의 단일 진입점. 인증/인가 및 라우팅 수행. | `Spring Cloud Gateway` |
| **Auth Service (User)** | 회원가입, 로그인 처리 및 JWT 토큰 발급. | `Spring Security`, `JWT` |
| **Auction Service** | 경매 물품 등록, 조회, 경매 상태(시작/종료) 관리, 낙찰자 선정. | `Spring Boot`, `JPA` |
| **Bid-Request Service** | 사용자의 입찰 요청을 접수하여 Kafka에 즉시 발행. (API 응답 속도 극대화) | `Spring Boot`, `Kafka Producer` |
| **Bidding Service** | Kafka의 입찰 메시지를 구독(Consume)하여 실제 입찰 유효성 검사 및 비즈니스 로직 처리. | `Spring Boot`, `Kafka Consumer` |
| **Notification Service** | `Bid Service`의 처리 결과(성공/실패)를 받아 SSE를 통해 클라이언트에 실시간 전송. | `Spring Boot`, `SSE` |
| **Analysis Service** | 입찰 메시지를 받고 있다가 경매가 종료되면 그래프를 생성, api endpoint로 그래프 데이터를 얻음. | `Spring Boot`, 

<br>

## 3. 핵심 기능

* **회원 관리**: JWT 기반의 안전한 회원가입 및 로그인 기능
* **경매 관리**: 경매 물품 등록, 스케줄러를 통한 자동 경매 시작/종료
* **비동기 입찰 처리**: 대규모 입찰 요청을 Kafka에 적재하여 순차적이고 안정적인 처리 보장
* **실시간 알림**: SSE를 활용하여 입찰 성공, 실패, 현재가 갱신 등 경매 상황을 실시간으로 클라이언트에 푸시

<br>

## 4. 기술 스택 (Environment)

### Backend
| Category | Stack | Version |
| :--- | :--- | :--- |
| **Runtime** | Java (JDK) | `[ 21 ]` |
| **Framework** | Spring Boot | `[ 3.5.5 ]` |
| | Spring Cloud | `[ 2023.1.1 ]` |
| | Spring Security | `[ 6.5 ]` |
| **Database** | `[PostgreSQL]` | `[ 15 ]` |
| **ORM** | Spring Data JPA | |
| **Message Broker** | Kafka | `[ 3.9.1 ]` |
| **Build Tool** | [Gradle] | `[ 3.0.22 ]` |

<br>

## 5. 시작하기 (Getting Started)

### Prerequisites (사전 준비물)

* Java (JDK) `[ 21 ]`
* [Gradle]
* Docker & Docker Compose
