<script setup>
import { computed, ref } from 'vue'
import apiClient from '@/api/axios.js'
import { useMutation, useQuery } from '@tanstack/vue-query'
import { useAuthStore } from '@/stores/auth.js'
import { useRouter } from 'vue-router'

//추천 번호를 저장할 변수
const recommendedNumbers = ref([])
const authStore = useAuthStore()
const router = useRouter()

//무작위로 추천하는 API
const { mutate: mutateRandom, isPending: isRandomPending } = useMutation({
  mutationFn: () => apiClient.post('/api/recommend/random'),
  onSuccess: (response) => {
    //성공 시, 응답으로 받은 번호들을 recommendedNumbers에 저장
    recommendedNumbers.value = response.data
  },
  onError: (error) => {
    alert(error.response?.data?.message || '번호 추천에 실패하였습니다.')
  },
})

//과거 통계를 기반으로 가장 많이 나온 번호에 가중치를 부여하여 추천하는 API
const { mutate: mutateStatistical, isPending: isStatisticalPending } = useMutation({
  mutationFn: () => apiClient.post('/api/recommend/statistical'),
  onSuccess: (response) => {
    recommendedNumbers.value = response.data
  },
  onError: (error) => {
    alert(error.response?.data?.message || '번호 추천에 실패하였습니다.')
  },
})

//최근 5주간 미출현 번호들 중에서 추천하는 API
const { mutate: mutateInfrequent, isPending: isInfrequentPending } = useMutation({
  mutationFn: () => apiClient.post('/api/recommend/infrequent'),
  onSuccess: (res) => {
    recommendedNumbers.value = res.data
  },
  onError: (error) => {
    alert(error.response?.data?.message || '번호 추천에 실패하였습니다.')
  },
})

//역대 가장 적게 나온 번호에 가중치를 부여하여 추천하는 API
const { mutate: mutateRare, isPending: isRarePending } = useMutation({
  mutationFn: () => apiClient.post('/api/recommend/rare'),
  onSuccess: (res) => {
    recommendedNumbers.value = res.data
  },
  onError: (error) => {
    alert(error.response?.data?.message || '번호 추천에 실패하였습니다.')
  },
})

//최근 6개월간 3번 이상 나온 번호 중에서 추천하는 API
const { mutate: mutateRecent6Months, isPending: isRecent6MonthsPending } = useMutation({
  mutationFn: () => apiClient.post('/api/recommend/recent6month'),
  onSuccess: (res) => {
    recommendedNumbers.value = res.data
  },
  onError: (error) => {
    alert(error.response?.data?.message || '번호 추천에 실패하였습니다.')
  },
})

//홀수 3개, 짝수 3개를 맞춰서 추천하는 API
const { mutate: mutateOddEven, isPending: isOddEvenPending } = useMutation({
  mutationFn: () => apiClient.post('/api/recommend/oddeven'),
  onSuccess: (res) => {
    recommendedNumbers.value = res.data
  },
  onError: (error) => {
    alert(error.response?.data?.message || '번호 추천에 실패하였습니다.')
  },
})

//두 가지 중 하나라도 진행중인지 확인하는 속성
const isPending = computed(
  () =>
    isRandomPending.value ||
    isStatisticalPending.value ||
    isInfrequentPending.value ||
    isRarePending.value ||
    isRecent6MonthsPending.value ||
    isOddEvenPending.value,
)

//랜덤 추천 버튼 클릭 시
const handleRandomRecommend = () => {
  //로그인 상태가 아닐 시
  if (!authStore.isLoggedIn) {
    alert('로그인이 필요한 기능입니다.')
    router.push('/login')
    return
  }
  //로그인 상태면 API 호출
  mutateRandom()
}

//통계 기반 추천 버튼 클릭 시
const handleStatisticalRecommend = () => {
  //로그인 상태가 아닐 시
  if (!authStore.isLoggedIn) {
    alert('로그인이 필요한 기능입니다.')
    router.push('/login')
    return
  }
  //로그인 상태면 API 호출
  mutateStatistical()
}

//최근 미출현 버튼 클릭 시
const handleInfrequentRecommend = () => {
  //로그인 상태가 아닐 시
  if (!authStore.isLoggedIn) {
    alert('로그인이 필요한 기능입니다.')
    router.push('/login')
    return
  }
  mutateInfrequent()
}

//희귀 번호 버튼 클릭 시
const handleRareRecommend = () => {
  //로그인 상태가 아닐 시
  if (!authStore.isLoggedIn) {
    alert('로그인이 필요한 기능입니다.')
    router.push('/login')
    return
  }
  mutateRare()
}

//6개월 분석 버튼 클릭 시
const handleRecent6MonthsRecommend = () => {
  //로그인 상태가 아닐 시
  if (!authStore.isLoggedIn) {
    alert('로그인이 필요한 기능입니다.')
    router.push('/login')
    return
  }
  mutateRecent6Months()
}

//홀/짝 조합 버튼 클릭 시
const handleOddEvenRecommend = () => {
  //로그인 상태가 아닐 시
  if (!authStore.isLoggedIn) {
    alert('로그인이 필요한 기능입니다.')
    router.push('/login')
    return
  }
  mutateOddEven()
}

//최신 당첨 번호 데이터 조회 로직
const { data: latestLottoData } = useQuery({
  queryKey: ['latest-lotto'],
  queryFn: async () => {
    const response = await apiClient.get('/api/main-page')
    return response.data
  },
})
</script>

<template>
  <div class="home-container">
    <div v-if="latestLottoData" class="latest-winning-section">
      <h2>{{ latestLottoData.round }}회차 당첨 번호</h2>
      <p class="draw-date">{{ latestLottoData.drawDate }}</p>
      <div class="number-balls">
        <span
          v-for="number in latestLottoData.winningNumbers"
          :key="number"
          class="ball winning-main"
        >
          {{ number }}
        </span>
        <span class="plus-sign">+</span>
        <span class="ball bonus">{{ latestLottoData.bonusNumber }}</span>
      </div>
      <div class="stats-container">
        <h3>🏆 {{ latestLottoData.round }}회차 추천 결과 🏆</h3>
        <p>
          1등 : <strong>{{ latestLottoData.firstPrizeCount }}</strong
          >개
        </p>
        <p>
          2등 : <strong>{{ latestLottoData.secondPrizeCount }}</strong
          >개
        </p>
        <p>
          3등 : <strong>{{ latestLottoData.thirdPrizeCount }}</strong
          >개
        </p>
        <p>
          4등 : <strong>{{ latestLottoData.fourthPrizeCount }}</strong
          >개
        </p>
        <p>
          5등 : <strong>{{ latestLottoData.fifthPrizeCount }}</strong
          >개
        </p>
      </div>
    </div>

    <hr v-if="latestLottoData" />

    <h1>로또 번호 추천</h1>
    <p>원하는 추천 방식을 선택하세요</p>

    <div class="button-group">
      <button @click="handleRandomRecommend" :disabled="isPending">🎲 랜덤 번호 추천</button>
      <button @click="handleStatisticalRecommend" :disabled="isPending">
        📊 통계 기반 번호 추천
      </button>
      <button @click="handleInfrequentRecommend" :disabled="isPending">
        ✨ 최근 미출현 번호 추천
      </button>
      <button @click="handleRareRecommend" :disabled="isPending">💎 희귀 번호 추천</button>
      <button @click="handleRecent6MonthsRecommend" :disabled="isPending">
        🏆 6개월 분석 번호 추천
      </button>
      <button @click="handleOddEvenRecommend" :disabled="isPending">☯️ 홀/짝 조합 번호 추천</button>
    </div>
    <div v-if="isPending" class="loading">로또 번호를 추천 중입니다...</div>
    <div v-if="recommendedNumbers.length > 0" class="result-container">
      <h2>추천 번호</h2>
      <div class="number-balls">
        <span v-for="number in recommendedNumbers" :key="number" class="ball">
          {{ number }}
        </span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  max-width: 600px;
  margin: 50px auto;
  padding: 20px;
  text-align: center;
}

.latest-winning-section {
  background-color: #f0faff;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 30px;
  text-align: center;
}

.draw-date {
  color: #666;
  font-size: 14px;
  margin-top: -10px;
  margin-bottom: 15px;
}

hr {
  border: none;
  border-top: 1px dashed #ccc;
  margin-bottom: 30px;
}

h1 {
  margin-bottom: 5px;
}

p {
  color: #555;
}

.button-group {
  margin: 20px 0;
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

button {
  padding: 10px 15px;
  font-size: 14px;
  cursor: pointer;
  border-radius: 8px;
  border: 1px solid #ccc;
  background-color: white;
  transition: all 0.2s;
}

button:hover:not(:disabled) {
  background-color: #f0f0f0;
  transform: translateY(-2px);
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.loading {
  margin-top: 20px;
  font-size: 18px;
  color: #555;
}

.result-container {
  margin-top: 30px;
  padding: 20px;
  border: 2px dashed #42b983;
  border-radius: 10px;
}

.number-balls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
}

.ball {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  background-color: #42b983;
}

.ball.winning-main {
  background-color: #0d6efd;
}

.ball.bonus {
  background-color: #ffc107;
}

.plus-sign {
  font-size: 24px;
  font-weight: bold;
  color: #6c757d;
  margin: 0 5px;
}

.stats-container {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
}

.stats-container h4 {
  margin-bottom: 10px;
}

.stats-container p {
  margin: 5px 0;
  font-size: 16px;
}
</style>
