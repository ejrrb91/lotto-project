# Lotto Helper

**과거 당첨 데이터를 분석하고 다양한 알고리즘을 통해 로또 번호를 추천받고, 다른 사용자들과 실시간으로 소통할 수 있는 풀스택 웹 애플리케이션입니다.**

<br/>

## 서비스 주소

**[https://lottohelper.kr](https://lottohelper.kr)**

<br/>

## 주요 기능

- ** 데이터 기반 로또 번호 추천**
    - `랜덤`, `통계 기반`, `최근 미출현`, `희귀 번호`, `6개월 분석`, `홀/짝 조합` 등 6가지의 다양한 알고리즘 제공
    - `@Scheduled`를 이용한 매주 토요일 자동 데이터 업데이트 기능
    - API를 통한 수동 데이터 업데이트 기능
- ** 사용자 기능**
    - JWT (Access Token, Refresh Token) 기반의 자체 회원가입 및 로그인
    - Google 소셜 로그인 (OAuth2)
    - Redis를 활용한 Refresh Token 관리로 안정적인 인증 상태 유지
    - 마이페이지에서 과거 추천 기록 및 회차별 당첨 결과 확인
- ** 실시간 채팅**
    - `WebSocket`과 `STOMP` 프로토콜을 이용한 회차별 실시간 채팅 기능
    - 페이지를 새로고침하거나 이동해도 채팅 세션 및 대화 내용 유지
- ** Docker 기반 배포**
    - `Docker Compose`를 사용하여 Frontend, Backend, DB, Cache, Web Server를 한번에 관리
    - `Nginx`를 이용한 리버스 프록시 및 `Let's Encrypt`를 통한 `HTTPS` 암호화 통신

<br/>

## ️ 기술 스택

| 구분           | 기술                                                                            |
|--------------|-------------------------------------------------------------------------------|
| **Backend**  | Java 17, Spring Boot, Spring Security, JPA, WebSocket, JWT, Redis, Jsoup      |
| **Frontend** | Vue.js, Pinia, Vue Query, Axios, StompJS, SockJS                              |
| **Database** | PostgreSQL                                                                    |
| **Infra**    | AWS EC2 (Ubuntu 22.04 LTS), Docker, Docker Compose, Nginx, Let's Encrypt, Git |

<br/>

## 시작하기

이 프로젝트는 로컬 개발 환경과 실제 서버 배포 환경에서 사용하는 설정이 다릅니다.

1. **Git Clone**
   ```bash
   git clone https://github.com/ejrrb91/lotto-project.git
   cd lotto-project
   ```

2. **.env 파일 생성**
   프로젝트 루트 경로에 `.env` 파일을 생성하고 아래 내용을 채워주세요.
   ```
   # JWT 비밀키 (KeyGenerator.java 실행하여 생성)
   JWT_SECRET_KEY=...

   # Google OAuth2 Client 정보
   SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID=...
   SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=...
   ```

3. **로컬용 Nginx 설정 적용**
   프로젝트 내의 `nginx/default.conf` 파일 내용을 아래의 **로컬 개발용 설정**으로 교체합니다.

   <details>
   <summary>nginx/default.conf (로컬 개발용)</summary>

   ```nginx
   server {
       listen 80;

       location / {
           root   /usr/share/nginx/html;
           index  index.html index.htm;
           try_files $uri $uri/ /index.html;
       }

       location /login {
           proxy_pass http://backend:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
       }
       
       location /oauth2 {
           proxy_pass http://backend:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
       }

       location /api {
           proxy_pass http://backend:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
       }

       location /ws {
           proxy_pass http://backend:8080;
           proxy_http_version 1.1;
           proxy_set_header Upgrade $http_upgrade;
           proxy_set_header Connection "Upgrade";
           proxy_set_header Host $host;
       }
   }
   ```
   </details>
   <br/>

4. **Docker 실행 (로컬)**
   ```bash
   docker-compose up --build -d
   ```

5. **접속**
   웹 브라우저에서 `http://localhost` 로 접속합니다.