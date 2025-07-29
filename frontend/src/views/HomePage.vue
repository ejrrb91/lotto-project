<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import apiClient from '@/api/axios.js'
import { useMutation, useQuery } from '@tanstack/vue-query'
import { useAuthStore } from '@/stores/auth.js'
import { useRouter } from 'vue-router'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

//추천 번호를 저장할 변수
const recommendedNumbers = ref([])

//인증 및 라우터
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

const createRecommendHandler = (mutateFn) => {
  return () => {
    if (!authStore.isLoggedIn) {
      alert('로그인이 필요한 기능입니다.')
      router.push('/login')
      return
    }
    mutateFn()
  }
}
const handleRandomRecommend = createRecommendHandler(mutateRandom)
const handleStatisticalRecommend = createRecommendHandler(mutateStatistical)
const handleInfrequentRecommend = createRecommendHandler(mutateInfrequent)
const handleRareRecommend = createRecommendHandler(mutateRare)
const handleRecent6MonthsRecommend = createRecommendHandler(mutateRecent6Months)
const handleOddEvenRecommend = createRecommendHandler(mutateOddEven)

//최신 당첨 번호 데이터 조회 로직
const { data: latestLottoData } = useQuery({
  queryKey: ['latest-lotto'],
  queryFn: async () => {
    const response = await apiClient.get('/api/main-page')
    return response.data
  },
})

//채팅 기능
const nicknameInput = ref('')
const chatUsername = ref('')
const messages = ref([])
const newMessage = ref('')
const isChatReady = ref(false)
const messagesAreaRef = ref('')
let stompClient = null

const roomId = computed(() => (latestLottoData.value ? latestLottoData.value.round + 1 : null))

onMounted(() => {
  const savedUsername = sessionStorage.getItem('chatUsername')
  if (savedUsername && authStore.isLoggedIn) {
    chatUsername.value = savedUsername
    isChatReady.value = true
  }
})
const startChat = () => {
  if (!authStore.isLoggedIn) {
    alert('채팅은 로그인이 필요한 기능입니다.')
    router.push('/login')
    return
  }
  if (!nicknameInput.value.trim()) {
    alert('사용할 닉네임을 입력해주세요.')
    return
  }
  chatUsername.value = nicknameInput.value
  isChatReady.value = true
}
watch(isChatReady, (newVal) => {
  if (newVal && roomId.value) {
    connect()
  }
})

const connect = () => {
  if (!chatUsername.value || !roomId.value) {
    return
  }
  const socket = new SockJS('/ws')
  stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    connectHeader: {
      Authorization: `Bearer ${authStore.accessToken}`,
    },
    onConnect: () => {
      console.log(`STOMP: ${roomId.value}회차 채팅방에 연결되었습니다.`)
      stompClient.subscribe(`/topic/chat/room/${roomId.value}`, (message) => {
        messages.value.push(JSON.parse(message.body))
        scrollToBottom()
      })
      //처음 입장할 때만 입장 메시지를 보냄
      if (nicknameInput.value.trim()) {
        stompClient.publish({
          destination: `/app/chat.addUser/${roomId.value}`,
          body: JSON.stringify({
            type: `ENTER`,
            roomId: roomId.value,
            sender: chatUsername.value,
            message: `${chatUsername.value}님이 입장하셨습니다.`,
          }),
        })
        nicknameInput.value = ''
      }
    },
    onStompError: (frame) => {
      console.error('STOMP Error :', frame)
      alert('채팅 서버에 연결할 수 없습니다. 로그인 상태를 확인해주세요.')
      isChatReady.value = false
    },
  })
  stompClient.activate()
}

const sendMessage = () => {
  if (newMessage.value.trim() && stompClient?.active) {
    stompClient.publish({
      destination: `/app/chat.sendMessage/${roomId.value}`,
      body: JSON.stringify({
        type: 'TALK',
        roomId: roomId.value,
        sender: chatUsername.value,
        message: newMessage.value,
      }),
    })
    newMessage.value = ''
  }
}

onUnmounted(() => {
  if (stompClient?.active) {
    stompClient.deactivate()
  }
})

const getMessageClass = (msg) => {
  if (msg.type === 'ENTER' || msg.type === 'LEAVE') {
    return 'system-message-container'
  }
  return msg.sender === chatUsername.value ? 'my-message-container' : 'other-message-container'
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesAreaRef.value) {
    messagesAreaRef.value.scrollTop = messagesAreaRef.value.scrollHeight
  }
}

const leaveChat = () => {
  if (stompClient?.active) {
    stompClient.publish({
      destination: `/app/chat.leaveUser/${roomId.value}`,
      body: JSON.stringify({ type: 'LEAVE', roomId: roomId.value, sender: chatUsername.value }),
    })
    stompClient.deactivate()
  }
  isChatReady.value = false
  messages.value = []
  chatUsername.value = ''
  nicknameInput.value = ''
}

watch(
  () => authStore.isLoggedIn,
  (isLoggedIn) => {
    if (!isLoggedIn && isChatReady.value) {
      leaveChat()
    }
  },
)
</script>

<template>
  <div class="page-container">
    <div class="lotto-content">
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
        <button @click="handleOddEvenRecommend" :disabled="isPending">
          ☯️ 홀/짝 조합 번호 추천
        </button>
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
    <div class="chat-content">
      <div v-if="!isChatReady" class="nickname-prompt">
        <h3>실시간 채팅</h3>
        <p v-if="roomId">{{ roomId }}회차 채팅에 참여하려면 닉네임을 입력해주세요.</p>
        <p v-else>채팅방 정보를 불러오는 중입니다...</p>
        <div class="nickname-input" v-if="roomId">
          <input v-model="nicknameInput" @keyup.enter="startChat" placeholder="닉네임 입력..." />
          <button @click="startChat">입장</button>
        </div>
      </div>

      <div v-else class="chat-room">
        <div class="chat-header">
          <h3>실시간 채팅: {{ roomId }}회차</h3>
          <button @click="leaveChat" class="leave-button">나가기</button>
        </div>
        <div class="messages-area" ref="messagesAreaRef">
          <div v-for="(msg, index) in messages" :key="index" :class="getMessageClass(msg)">
            <div v-if="msg.type === 'ENTER' || msg.type === 'LEAVE'" class="system-message">
              {{ msg.message }}
            </div>
            <div v-else class="message-bubble">
              <span class="sender">{{ msg.sender }}</span>
              <span class="message-content">{{ msg.message }}</span>
            </div>
          </div>
        </div>
        <div class="input-area">
          <input
            v-model="newMessage"
            @keyup.enter="sendMessage"
            placeholder="메시지를 입력하세요..."
          />
          <button @click="sendMessage">전송</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* --- 전체 레이아웃 --- */
.page-container {
  display: flex;
  height: calc(100vh - 72px); /* 헤더 높이를 제외한 전체 높이 */
}

/* --- 왼쪽: 로또 콘텐츠 --- */
.lotto-content {
  flex: 3;
  padding: 2rem;
  border-right: 1px solid #ddd;
  overflow-y: auto;
  text-align: center;
}

/* --- 오른쪽: 채팅 콘텐츠 --- */
.chat-content {
  flex: 2;
  display: flex;
  flex-direction: column;
  background-color: #f9f9f9;
  position: relative; /* 토글 버튼 위치의 기준점이 됨 */
  transition: flex 0.3s ease-in-out;
}

.chat-content.collapsed {
  flex: 0 0 50px;
}

.chat-toggle-button {
  position: absolute;
  top: 50%;
  left: 0;
  transform: translate(-50%, -50%);
  width: 28px;
  height: 56px;
  background-color: #007bff;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 14px 0 0 14px;
  cursor: pointer;
  z-index: 10;
  font-size: 20px;
  font-weight: bold;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.chat-toggle-button:hover {
  background-color: #0056b3;
}

.chat-ui-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  overflow: hidden;
}

/* --- 로또 콘텐츠 내부 스타일 --- */
.latest-winning-section {
  background-color: #f0faff;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 30px;
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
  margin: 30px 0;
}

h1 {
  margin-bottom: 5px;
}

p {
  color: #555;
}

.button-group {
  margin: 20px 0;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 10px;
}

button {
  padding: 10px 15px;
  font-size: 14px;
  cursor: pointer;
  border-radius: 8px;
  border: 1px solid #ccc;
  background-color: white;
  transition: all 0.2s ease-in-out; /* [수정] 부드러운 전환 효과 */
}

button:hover:not(:disabled) {
  transform: scale(1.05); /* 5% 확대 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
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
  flex-wrap: wrap;
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
  background-color: #6c757d;
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

.stats-container h3 {
  margin-bottom: 15px;
}

.stats-container p {
  margin: 5px 0;
  font-size: 16px;
}

/* --- 채팅 UI 스타일 --- */
.nickname-prompt {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  text-align: center;
  padding: 2rem;
}

.nickname-input {
  display: flex;
  margin-top: 1rem;
}

.nickname-input input {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.nickname-input button {
  padding: 0.5rem 1rem;
  margin-left: 0.5rem;
  border: none;
  background-color: #007bff;
  color: white;
  border-radius: 4px;
}

.chat-room {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.chat-header {
  position: relative; /* [수정] 나가기 버튼 위치의 기준점 역할 */
  padding: 1rem;
  background-color: #e9ecef;
  border-bottom: 1px solid #ddd;
  text-align: center;
  display: flex; /* [추가] 자식 요소들을 정렬하기 위해 flex 사용 */
  justify-content: center; /* [추가] 제목을 중앙에 정렬 */
  align-items: center; /* [추가] 세로 중앙 정렬 */
}

.chat-header h3 {
  margin: 0;
  font-size: 1.2rem;
}

/* [수정] 나가기 버튼 스타일 */
.leave-button {
  position: absolute; /* 헤더를 기준으로 위치를 잡음 */
  right: 1rem; /* 오른쪽에서 1rem 만큼 떨어짐 */
  padding: 4px 8px;
  font-size: 12px;
  background-color: #6c757d;
  color: white;
  border: none;
}

.leave-button:hover {
  background-color: #5a6268;
}

.messages-area {
  flex-grow: 1;
  overflow-y: auto;
  padding: 1rem;
  display: flex;
  flex-direction: column;
}

.my-message-container,
.other-message-container,
.system-message-container {
  display: flex;
  margin-bottom: 0.75rem;
  max-width: 100%;
}

.my-message-container {
  justify-content: flex-end;
}

.other-message-container {
  justify-content: flex-start;
}

.system-message-container {
  justify-content: center;
}

.message-bubble {
  padding: 0.5rem 1rem;
  border-radius: 1.25rem;
  max-width: 100%;
  word-wrap: break-word;
  background-color: #fff;
  color: black;
  border: 1px solid #e9ecef;
}

.sender {
  font-weight: bold;
  display: block;
  font-size: 0.8rem;
  margin-bottom: 0.2rem;
  color: #666;
}

.my-message-container .sender {
  display: none;
}

.system-message {
  font-style: italic;
  color: #888;
  font-size: 0.9rem;
}

.input-area {
  display: flex;
  padding: 1rem;
  border-top: 1px solid #ddd;
  background-color: #f8f9fa;
}

.input-area input {
  flex-grow: 1;
  border: 1px solid #ccc;
  border-radius: 20px;
  padding: 0.5rem 1rem;
}

.input-area button {
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 0.5rem 1.5rem;
  margin-left: 0.5rem;
}
</style>
