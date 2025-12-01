# [MSA 기반 실시간 경매 플랫폼]

> 개발 기간 : 25.09.16~25.11.30
> <br>
> 개발 인원 : 1인

## 1. 프로젝트 개요

본 프로젝트는 대규모 트래픽과 실시간 동시 입찰 요청을 안정적으로 처리하는 것을 목표로 설계된 **마이크로서비스 아키텍처(MSA) 기반 경매 플랫폼**입니다.

주요 비즈니스 로직(입찰 요청, 처리)을 **비동기 메시지 큐(Kafka)**를 통해 분리하여 시스템의 안정성과 확장성을 확보했습니다. 또한 **SSE(Server-Sent Events)**를 통해 클라이언트에게 실시간으로 입찰 현황 및 결과를 전송합니다.

<br>

## 2. 시스템 아키텍처(Cloud)
<img width="892" height="576" alt="Image" src="https://github.com/user-attachments/assets/352542d9-d8dc-46ad-b698-50afd3cf42a8" />

### 서비스 구성

| Service | 역할 |
| :--- | :--- |
| **API Gateway** | 모든 클라이언트 요청의 단일 진입점. 인증/인가 및 라우팅 수행. |
| **Auth Service** | 회원가입, 로그인 처리 및 JWT 토큰 발급. |
| **Auction Service** | 경매 물품 등록, 조회, 경매 상태(시작/종료) 관리, 낙찰자 선정. |
| **Bid-Request Service** | 사용자의 입찰 요청을 접수하여 Kafka에 즉시 발행. (API 응답 속도 극대화) |
| **Bidding Service** | Kafka의 입찰 메시지를 구독(Consume)하여 실제 입찰 유효성 검사 및 비즈니스 로직 처리. |
| **Notification Service** | `Bidding Service`의 처리 결과(성공/실패)를 받아 SSE를 통해 클라이언트에 실시간 전송. |
| **Analysis Service** | 입찰 메시지를 받고 있다가 경매가 종료되면 그래프를 생성, api endpoint로 그래프 데이터를 얻음. |
| **Bookmark Service** | 사용자의 북마크를 담당하는 서비스 |
| **Search Service** | Elasticsearch의 역인덱스를 활용해 빠르고 효율적인 검색 성능을 제공 |
| **Web Bff Service** | 내부에서 API를 호출하여 마이크로 서비스들을 적절히 조합해 반환하는 서비스 |

<br>

## 3. 핵심 기능

* **회원 관리**: JWT 기반의 안전한 회원가입 및 로그인 기능
* **경매 관리**: 경매 물품 등록, 스케줄러를 통한 자동 경매 시작/종료
* **비동기 입찰 처리**: 대규모 입찰 요청을 Kafka에 적재하여 순차적이고 안정적인 처리 보장
* **실시간 알림**: SSE를 활용하여 입찰 성공, 실패, 현재가 갱신 등 경매 상황을 실시간으로 클라이언트에 푸시
* **경매 결과 비교**: 종료된 경매의 입찰들을 시각화하여 흐름을 파악할 수 있음

<br>

## 4. 기술 스택 (Environment)

### 공통
| Category | Stack | Version | Description |
| :--- | :--- | :--- | :--- |
| 🧩 **Framework** | Spring Boot | `3.5.5` | 백엔드 메인 프레임워크 |
|  | Spring Cloud | `2025.0.0` | 마이크로서비스 구성, 공동인증 |
| 🗄️ **Database** | PostgreSQL | `15` | 주 데이터 저장소 |
| 💻 **Frontend** | Next.js | `15` | UI 구성, SSR, CSR |
|  | TanStack Query | `5.90.0` | API 캐싱, 동기화 |
| ⚙️ **Etc** | Gradle | `3.0.22` | 빌드 |
|  | Kafka | `3.9.1` | 비동기 메시지 큐 |
|  | QueryDSL | `5.1.0` | 동적 검색 |
|  | Elasticsearch | `9.2.0` | Inverted Index |
|  | Consul | `1.15.4` | 서비스 디스커버리 |
|  | Sse | `3.5.5` | 실시간 통신 |

### Local
| Category | Stack | Version | Description |
| :--- | :--- | :--- | :--- |
| 🧩 **ETC** | Nginx | `1.29.1` | K8s 기반 서비스 오케스트레이션 |
|  | Docker Compose | `2.35.1` | 컨테이너 관리 |
|  | Consul | `1.15.4` | 서비스 디스커버리 |
|  | Spring Cloud Loadbalancer | `2025.0.0` | 서비스 로드 밸런서 |

### Cloud
| Category | Stack | Version | Description |
| :--- | :--- | :--- | :--- |
| 🧩 **Infra & Network** | EKS | `1.3.0` | K8s 기반 서비스 오케스트레이션 |
|  | EC2 | `latest` | 워커노드 |
|  | ALB | `latest` | 외부 트래픽 라우팅(Load Balancer) |
|  | Ingress Controller | `latest` | K8s 내부 라우팅 |
|  | VPC | `-` | 네트워크 구성(Subnet) |
| 🪣 **Storage** | RDS(PostgreSQL) | `15` | 운영 DB |
|  | S3 | `-` | 정적 파일 저장 |
| 🔧 **CI/CD** | GitHub Actions | `latest` | CI/CD 자동화 |
|  | Docker Hub | `-` | 컨테이너 이미지 저장소


## 5. 부하 테스트(K6)
<img width="1792" height="935" alt="Image" src="https://github.com/user-attachments/assets/d48df568-d0b0-4871-b754-998bb46063c6" />

- 조건 : 로컬, 최대 5000명의 가상유저가 입찰 시도.
  - http_req_duration -> 요청을 보내고 응답을 받을 때까지 걸린 총 시간.
  - http_req_wating -> 서버가 데이터를 처리하느라 걸린 시간.
  - http_req_connecting -> 네트워크 연결
  - http_req_failed -> 요청 실패 비율
  - http_reqs -> 초당 n건의 트랜잭션 처리

- **결과** : 평소에는 0.3초대로 빠르지만 요청이 몰릴 때는 응답 속도가 약 4초까지 지연되는 현상이 관측되었음. 이는 로컬에서의 cpu, 메모리 한계 때문(Docker, 부하 테스트 툴을 하나의 PC에서 실행) 4초까지 지연이 되지만 실패하는 경우는 없었음. 실제 운영환경에서는 Kafka 파티션을 늘리고 스케일 아웃을 하면 4초 지연은 1초 미만으로 단축될거라 예상함.


**Branch Info:**
- `main` 🟢 : Cloud 환경용
- `dev01` 🔵 : Local 개발용
