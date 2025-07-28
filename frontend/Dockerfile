# 1. 빌드(build) 단계
# node:20-alpine 이미지를 사용하여 빌드 환경 설정
FROM node:20-alpine AS build

# 작업 디렉토리를 /app으로 설정
WORKDIR /app

# package.json과 package.lock.json 파일을 먼저 복사하여 의존성 설치
# (파일 내용이 바뀌지 않으면, Docker는 이 단계를 캐시하여 재사용)
COPY package*.json ./
RUN npm install

# 나머지 모든 소스코드를 복사
COPY . .

# Vue.js 프로젝트를 빌드하여 정적 파일(HTML, CSS, JS)을 생성
RUN npm run build

# 2. 서빙(serve) 단계
# ngnix:stable-alpine 이미지를 사용하여 웹 서버 환경 설정
FROM nginx:stable-alpine

# 위 'build'단계에서 생성된 빌드 결과물(dist 폴더)을
# Nginx가 정적 파일을 제공하는 기본 폴더(/usr/share/nginx/html)로 복사합니다.
COPY --from=build /app/dist /usr/share/nginx/html

# 컨테이너의 80번 포트를 외부로 노출
EXPOSE 80

# Nginx 웹 서버를 실행
CMD ["nginx", "-g", "daemon off;"]
