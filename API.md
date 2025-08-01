# Lotto Helper API 명세서

## User

### `POST /api/users/register`

- **설명:** 신규 회원 가입
- **인증:** 필요 없음
- **Request Body:**
    ```json
    {
      "email": "test@example.com",
      "password": "password123",
      "nickname": "testuser"
    }
    ```
- **Success Response (201 CREATED):**
    ```
    회원가입이 완료되었습니다.
    ```
- **Error Response (409 Conflict):** 이메일이 이미 존재할 경우

### `POST /api/users/login`

- **설명:** 이메일과 비밀번호로 로그인
- **인증:** 필요 없음
- **Request Body:**
    ```json
    {
      "email": "test@example.com",
      "password": "password123"
    }
    ```
- **Success Response (200 OK):**
    ```json
    {
      "accessToken": "ey...",
      "refreshToken": "ey..."
    }
    ```

### `POST /api/users/reissue`

- **설명:** Access Token 만료 시 Refresh Token으로 재발급
- **인증:** Header에 `Authorization: Bearer [RefreshToken]` 필요
- **Success Response (200 OK):**
    ```json
    {
      "accessToken": "ey...",
      "refreshToken": "ey..."
    }
    ```

### `POST /api/users/logout`

- **설명:** 로그아웃 처리 (Redis에 저장된 Refresh Token 삭제)
- **인증:** Header에 `Authorization: Bearer [AccessToken]` 필요
- **Success Response (200 OK):**
    ```
    로그아웃 되었습니다.
    ```

## Recommendation

### `POST /api/recommend/{type}`

- **설명:** 지정된 타입으로 로또 번호 6개를 추천받습니다.
- **인증:** Header에 `Authorization: Bearer [AccessToken]` 필요
- **Endpoint `{type}` 종류:**
    - `random`, `statistical`, `infrequent`, `rare`, `recent6month`, `oddeven`
- **Success Response (200 OK):**
    ```json
    [5, 11, 20, 30, 40, 45]
    ```

## Page Data

### `GET /api/main-page`

- **설명:** 메인 페이지에 필요한 최신 회차 당첨 정보 및 추천 통계 데이터를 조회합니다.
- **인증:** 필요 없음
- **Success Response (200 OK):**
    ```json
    {
      "round": 1183,
      "drawDate": "2025-08-23",
      "winningNumbers": [7, 9, 11, 21, 30, 35],
      "bonusNumber": 43,
      "firstPrizeCount": 0,
      "secondPrizeCount": 0,
      "thirdPrizeCount": 0,
      "fourthPrizeCount": 0,
      "fifthPrizeCount": 0
    }
    ```

### `GET /api/my-page`

- **설명:** 마이페이지에서 본인의 과거 추천 기록 및 당첨 결과를 조회합니다.
- **인증:** Header에 `Authorization: Bearer [AccessToken]` 필요
- **Success Response (200 OK):**
    ```json
    {
      "latestRound": { ... }, // 최신 회차 정보
      "myRecommendations": [
        {
          "id": 1,
          "recommendedAt": "2025-08-23T12:00:00",
          "algorithmType": "RANDOM",
          "lottoRound": 1184,
          "num1": 5, ... , "num6": 45,
          "matchCount": null,
          "isBonusMatched": null,
          "rank": "추첨 전",
          "winningNumbers": null,
          "bonusNumber": null
        }
      ]
    }
    ```

## ️ Data Update

### `POST /api/lotto/update`

- **설명:** 수동으로 로또 당첨 번호 데이터 수집 및 업데이트를 실행합니다.
- **인증:** Header에 `Authorization: Bearer [AccessToken]` 필요
- **Success Response (200 OK):**
    ```
    XX개의 새로운 회차 데이터가 저장되었습니다.
    ```