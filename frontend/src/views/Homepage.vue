<script setup>
import { computed, ref } from 'vue'
import apiClient from '@/api/axios.js'
import { useMutation } from '@tanstack/vue-query'
import { useAuthStore } from '@/stores/auth.js'
import { useRouter } from 'vue-router'

//추천 번호를 저장할 변수
const recommendedNumbers = ref([])
const authStore = useAuthStore()
const router = useRouter()

//랜덤 추천 로직 정의
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

//통계 기반 추천 로직 정의
const { mutate: mutateStatistical, isPending: isStatisticalPending } = useMutation({
  mutationFn: () => apiClient.post('api/recommend/statistical'),
  onSuccess: (response) => {
    recommendedNumbers.value = response.data
  },
  onError: (error) => {
    alert(error.response?.data?.message || '번호 추천에 실피하였습니다.')
  },
})

//두 가지 중 하나라도 진행중인지 확인하는 속성
const isPending = computed(() => isRandomPending.value || isStatisticalPending.value)

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
</script>

<template>
  <div class="home-container">
    <h1>로또 번호 추천</h1>
    <p>원하는 추천 방식을 선택하세요</p>

    <div class="button-group">
      <button @click="handleRandomRecommend" :disabled="isRandomPending || isStatisticalPending">
        🎲 랜덤 번호 추천
      </button>
      <button
        @click="handleStatisticalRecommend"
        :disabled="isRandomPending || isStatisticalPending"
      >
        📊 통계 기반 추천
      </button>
    </div>
    <div v-if="isRandomPending || isStatisticalPending" class="loading">
      로또 번호를 추천 중입니다...
    </div>
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

.button-group {
  margin: 20px 0;
  display: flex;
  justify-content: center;
  gap: 20px;
}

button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  border-radius: 8px;
  border: 1px solid #ccc;
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
  gap: 10px;
  margin-top: 10px;
}

.ball {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #42b983;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}
</style>
