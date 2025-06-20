<script setup>
import apiClient from '@/api/axios.js'
import { useQuery } from '@tanstack/vue-query'

const {
  isLoading,
  isError,
  data: responseData,
  error,
} = useQuery({
  queryKey: ['my-page-data'],
  queryFn: async () => {
    const response = await apiClient.get('/api/my-page/recommendations')
    return response.data
  },
})

//특정 번호가 당첨 번호에 포함되는지 확인
const isWinningNumber = (number, winningNumbers) => {
  //winningNumbers가 아직 로드 되지 않았으면 false 반환
  if (!winningNumbers) {
    return false
  }
  //Set을 사용하면 배열보다 빠르게 포함 여부를 확인할 수 있음
  return new Set(winningNumbers).has(number)
}

//날짜 형식을 'YYYY-MM-DD HH:mm' 형식으로 바꾸는 함수
const formatDate = (dateString) => {
  const date = new Date(dateString)

  // 'ko-KR' 로케일에 맞는 짧은 날짜와 시간 형식으로 변환.
  // 예시 출력: "24. 6. 20. 오후 11:53"
  return date.toLocaleString('ko-KR', {
    dateStyle: 'short',
    timeStyle: 'short',
  })
}
</script>

<template>
  <div class="mypage-container">
    <h1>마이페이지</h1>

    <div v-if="isLoading" class="loading-state">
      <p>데이터를 불러오는 중입니다.</p>
    </div>

    <div v-else-if="isError" class="error-state">
      <p>오류가 발생했습니다. : {{ error.message }}</p>
    </div>

    <div v-else-if="responseData">
      <div class="latest-winning-numbers">
        <h2>최신 회차 당첨 번호</h2>
        <div v-if="responseData.latestWinningNumbers">
          <p class="draw-date">({{ responseData.round }}회차 / {{ responseData.drawDate }} 추첨)</p>
          <div class="number-balls">
            <span
              v-for="number in responseData.latestWinningNumbers"
              :key="number"
              class="ball winning-main"
            >
              {{ number }}
            </span>
            <span class="plus-sign">+</span>
            <span class="ball bonus">{{ responseData.bonusNumber }}</span>
          </div>
        </div>
        <div v-else>
          <p>최신 당첨 번호 정보가 없습니다.</p>
        </div>
      </div>

      <hr />

      <h2>나의 추천 기록</h2>

      <div
        v-if="!responseData.myRecommendations || responseData.myRecommendations.length === 0"
        class="no-data"
      >
        <p>추천 기록이 없습니다.</p>
      </div>
      <table v-else class="recommendation-table">
        <thead>
          <tr>
            <th>추천일시</th>
            <th>추천방식</th>
            <th>추천번호</th>
            <th>일치하는 개수</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="rec in responseData.myRecommendations" :key="rec.id">
            <td>{{ formatDate(rec.recommendedAt) }}</td>
            <td>{{ rec.algorithmType }}</td>
            <td class="number-cell">
              <span
                :class="{
                  ball: true,
                  'winning-rec': isWinningNumber(rec.num1, responseData.latestWinningNumbers),
                }"
                >{{ rec.num1 }}</span
              >
              <span
                :class="{
                  ball: true,
                  'winning-rec': isWinningNumber(rec.num2, responseData.latestWinningNumbers),
                }"
                >{{ rec.num2 }}</span
              >
              <span
                :class="{
                  ball: true,
                  'winning-rec': isWinningNumber(rec.num3, responseData.latestWinningNumbers),
                }"
                >{{ rec.num3 }}</span
              >
              <span
                :class="{
                  ball: true,
                  'winning-rec': isWinningNumber(rec.num4, responseData.latestWinningNumbers),
                }"
                >{{ rec.num4 }}</span
              >
              <span
                :class="{
                  ball: true,
                  'winning-rec': isWinningNumber(rec.num5, responseData.latestWinningNumbers),
                }"
                >{{ rec.num5 }}</span
              >
              <span
                :class="{
                  ball: true,
                  'winning-rec': isWinningNumber(rec.num6, responseData.latestWinningNumbers),
                }"
                >{{ rec.num6 }}</span
              >
            </td>
            <td v-if="rec.matchCount !== null">
              <strong>{{ rec.matchCount }}개</strong>
            </td>
            <td v-else>-</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.mypage-container {
  max-width: 900px;
  margin: 40px auto;
  padding: 20px;
}

h1,
h2 {
  text-align: center;
}

hr {
  margin: 40px 0;
  border: none;
  border-top: 1px dashed #ccc;
}

.latest-winning-numbers {
  background-color: #f0faff;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 20px;
  text-align: center;
}

.draw-date {
  color: #666;
  font-size: 14px;
  margin-top: -10px;
  margin-bottom: 15px;
}

.loading-state,
.error-state,
.no-data {
  text-align: center;
  margin-top: 50px;
  color: #666;
  font-size: 18px;
}

.recommendation-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

th,
td {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: center;
  vertical-align: middle;
}

th {
  background-color: #f8f9fa;
}

tr:nth-child(even) {
  background-color: #f2f2f2;
}

.number-cell {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 5px;
  flex-wrap: wrap;
}

.ball {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  color: white;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  font-weight: bold;
  transition: all 0.3s;
  background-color: #6c757d;
}

.ball.winning-main {
  background-color: #0d6efd;
}

.ball.winning-rec {
  background-color: #dc3545;
  transform: scale(1.1);
  box-shadow: 0 0 10px rgba(220, 53, 69, 0.7);
}

.plus-sign {
  font-size: 24px;
  font-weight: bold;
  color: #6c757d;
  margin: 0 5px;
}

.ball.bonus {
  background-color: #ffc107; /* 보너스 번호 색깔 (노란 계열) */
}
</style>
