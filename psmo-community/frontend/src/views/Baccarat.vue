<template>
  <div class="baccarat-container">
    <!-- 게임 헤더 -->
    <div class="game-header">
      <h1>🎰 바카라</h1>
      <div class="balance">
        <span class="label">보유 칩:</span>
        <span class="amount">{{ balance.toLocaleString() }}</span>
        <span class="unit">원</span>
      </div>
    </div>

    <!-- 게임 결과 히스토리 -->
    <div class="history-section">
      <h3>게임 히스토리</h3>
      <div class="history-grid">
        <div
          v-for="(result, index) in history"
          :key="index"
          :class="['history-item', result.winner]"
        >
          {{ result.winner === "player" ? "P" : result.winner === "banker" ? "B" : "T" }}
        </div>
      </div>
    </div>

    <!-- 게임 테이블 -->
    <div class="game-table">
      <!-- 플레이어 영역 -->
      <div class="player-area">
        <h3>플레이어</h3>
        <div class="cards">
          <div
            v-for="(card, index) in playerCards"
            :key="index"
            :class="['card', card.suit]"
          >
            <div class="card-value">{{ card.display }}</div>
            <div class="card-suit">{{ getSuitSymbol(card.suit) }}</div>
          </div>
        </div>
        <div v-if="playerCards.length > 0" class="score">
          점수: <strong>{{ playerScore }}</strong>
        </div>
      </div>

      <!-- 뱅커 영역 -->
      <div class="banker-area">
        <h3>뱅커</h3>
        <div class="cards">
          <div
            v-for="(card, index) in bankerCards"
            :key="index"
            :class="['card', card.suit]"
          >
            <div class="card-value">{{ card.display }}</div>
            <div class="card-suit">{{ getSuitSymbol(card.suit) }}</div>
          </div>
        </div>
        <div v-if="bankerCards.length > 0" class="score">
          점수: <strong>{{ bankerScore }}</strong>
        </div>
      </div>
    </div>

    <!-- 베팅 영역 -->
    <div class="betting-section">
      <div class="bet-options">
        <div
          :class="['bet-option', { active: currentBet === 'player' }]"
          @click="selectBet('player')"
        >
          <div class="bet-title">플레이어</div>
          <div class="bet-odds">1:1</div>
        </div>
        <div
          :class="['bet-option', { active: currentBet === 'tie' }]"
          @click="selectBet('tie')"
        >
          <div class="bet-title">타이</div>
          <div class="bet-odds">1:8</div>
        </div>
        <div
          :class="['bet-option', { active: currentBet === 'banker' }]"
          @click="selectBet('banker')"
        >
          <div class="bet-title">뱅커</div>
          <div class="bet-odds">1:0.95</div>
        </div>
      </div>

      <!-- 베팅 금액 입력 -->
      <div class="bet-amount-section">
        <h3>베팅 금액</h3>
        <div class="chip-buttons">
          <button
            v-for="chip in chipValues"
            :key="chip"
            @click="addBetAmount(chip)"
            class="chip-btn"
          >
            {{ chip.toLocaleString() }}
          </button>
          <button @click="betAmount = 0" class="chip-btn reset">리셋</button>
        </div>
        <div class="bet-input">
          <input
            v-model.number="betAmount"
            type="number"
            placeholder="베팅 금액"
            :disabled="isPlaying"
          />
        </div>
      </div>

      <!-- 게임 컨트롤 -->
      <div class="game-controls">
        <button
          @click="startGame"
          :disabled="!canStartGame"
          class="btn-primary"
        >
          {{ isPlaying ? "게임 진행중..." : "게임 시작" }}
        </button>
        <button @click="resetGame" class="btn-secondary">새 게임</button>
      </div>
    </div>

    <!-- 게임 결과 모달 -->
    <div v-if="showResult" class="result-modal" @click="closeResult">
      <div class="result-content" @click.stop>
        <h2>{{ resultMessage }}</h2>
        <div class="result-details">
          <div class="result-row">
            <span>플레이어:</span>
            <strong>{{ playerScore }}</strong>
          </div>
          <div class="result-row">
            <span>뱅커:</span>
            <strong>{{ bankerScore }}</strong>
          </div>
          <div class="result-row payout">
            <span>{{ winAmount >= 0 ? "획득:" : "손실:" }}</span>
            <strong :class="{ win: winAmount > 0, lose: winAmount < 0 }">
              {{ Math.abs(winAmount).toLocaleString() }}원
            </strong>
          </div>
        </div>
        <button @click="closeResult" class="btn-primary">확인</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";

// 게임 상태
const balance = ref(1000000); // 초기 보유 금액
const betAmount = ref(0);
const currentBet = ref(null); // 'player', 'banker', 'tie'
const isPlaying = ref(false);
const showResult = ref(false);
const resultMessage = ref("");
const winAmount = ref(0);

// 카드 및 점수
const playerCards = ref([]);
const bankerCards = ref([]);
const playerScore = ref(0);
const bankerScore = ref(0);

// 게임 히스토리
const history = ref([]);

// 칩 옵션
const chipValues = [1000, 5000, 10000, 50000, 100000, 500000];

// 카드 덱
const suits = ["hearts", "diamonds", "clubs", "spades"];
const values = [
  { value: 1, display: "A" },
  { value: 2, display: "2" },
  { value: 3, display: "3" },
  { value: 4, display: "4" },
  { value: 5, display: "5" },
  { value: 6, display: "6" },
  { value: 7, display: "7" },
  { value: 8, display: "8" },
  { value: 9, display: "9" },
  { value: 0, display: "10" },
  { value: 0, display: "J" },
  { value: 0, display: "Q" },
  { value: 0, display: "K" },
];

// Computed
const canStartGame = computed(() => {
  return currentBet.value && betAmount.value > 0 && betAmount.value <= balance.value && !isPlaying.value;
});

// Methods
const selectBet = (bet) => {
  if (!isPlaying.value) {
    currentBet.value = bet;
  }
};

const addBetAmount = (amount) => {
  if (!isPlaying.value) {
    betAmount.value += amount;
    if (betAmount.value > balance.value) {
      betAmount.value = balance.value;
    }
  }
};

const getSuitSymbol = (suit) => {
  const symbols = {
    hearts: "♥",
    diamonds: "♦",
    clubs: "♣",
    spades: "♠",
  };
  return symbols[suit];
};

const createDeck = () => {
  const deck = [];
  for (let suit of suits) {
    for (let card of values) {
      deck.push({ ...card, suit });
    }
  }
  return deck;
};

const shuffleDeck = (deck) => {
  const shuffled = [...deck];
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
  }
  return shuffled;
};

const calculateScore = (cards) => {
  return cards.reduce((sum, card) => sum + card.value, 0) % 10;
};

const drawCard = (deck) => {
  return deck.pop();
};

const startGame = async () => {
  if (!canStartGame.value) return;

  isPlaying.value = true;
  playerCards.value = [];
  bankerCards.value = [];
  playerScore.value = 0;
  bankerScore.value = 0;

  // 덱 생성 및 섞기
  let deck = shuffleDeck(createDeck());

  // 초기 2장씩 분배 (애니메이션 효과)
  await dealInitialCards(deck);

  // 세 번째 카드 규칙 적용
  await applyThirdCardRules(deck);

  // 결과 계산
  determineWinner();
};

const dealInitialCards = async (deck) => {
  // 플레이어 첫 번째 카드
  await delay(500);
  playerCards.value.push(drawCard(deck));

  // 뱅커 첫 번째 카드
  await delay(500);
  bankerCards.value.push(drawCard(deck));

  // 플레이어 두 번째 카드
  await delay(500);
  playerCards.value.push(drawCard(deck));

  // 뱅커 두 번째 카드
  await delay(500);
  bankerCards.value.push(drawCard(deck));

  playerScore.value = calculateScore(playerCards.value);
  bankerScore.value = calculateScore(bankerCards.value);
};

const applyThirdCardRules = async (deck) => {
  const playerTotal = playerScore.value;
  const bankerTotal = bankerScore.value;

  // 내츄럴 (8 또는 9) - 추가 카드 없음
  if (playerTotal >= 8 || bankerTotal >= 8) {
    return;
  }

  let playerThirdCard = null;

  // 플레이어 세 번째 카드 규칙
  if (playerTotal <= 5) {
    await delay(800);
    playerThirdCard = drawCard(deck);
    playerCards.value.push(playerThirdCard);
    playerScore.value = calculateScore(playerCards.value);
  }

  // 뱅커 세 번째 카드 규칙
  if (playerThirdCard === null) {
    // 플레이어가 카드를 받지 않은 경우
    if (bankerTotal <= 5) {
      await delay(800);
      bankerCards.value.push(drawCard(deck));
      bankerScore.value = calculateScore(bankerCards.value);
    }
  } else {
    // 플레이어가 세 번째 카드를 받은 경우
    const playerThirdValue = playerThirdCard.value;
    let bankerDraws = false;

    if (bankerTotal <= 2) {
      bankerDraws = true;
    } else if (bankerTotal === 3) {
      bankerDraws = playerThirdValue !== 8;
    } else if (bankerTotal === 4) {
      bankerDraws = [2, 3, 4, 5, 6, 7].includes(playerThirdValue);
    } else if (bankerTotal === 5) {
      bankerDraws = [4, 5, 6, 7].includes(playerThirdValue);
    } else if (bankerTotal === 6) {
      bankerDraws = [6, 7].includes(playerThirdValue);
    }

    if (bankerDraws) {
      await delay(800);
      bankerCards.value.push(drawCard(deck));
      bankerScore.value = calculateScore(bankerCards.value);
    }
  }
};

const determineWinner = () => {
  let winner;
  let payout = 0;

  if (playerScore.value > bankerScore.value) {
    winner = "player";
    if (currentBet.value === "player") {
      payout = betAmount.value * 2; // 1:1
      resultMessage.value = "플레이어 승리! 🎉";
    } else {
      payout = 0;
      resultMessage.value = "플레이어 승리 (패배)";
    }
  } else if (bankerScore.value > playerScore.value) {
    winner = "banker";
    if (currentBet.value === "banker") {
      payout = betAmount.value * 1.95; // 1:0.95 (수수료 5%)
      resultMessage.value = "뱅커 승리! 🎉";
    } else {
      payout = 0;
      resultMessage.value = "뱅커 승리 (패배)";
    }
  } else {
    winner = "tie";
    if (currentBet.value === "tie") {
      payout = betAmount.value * 9; // 1:8
      resultMessage.value = "타이! 🎉";
    } else {
      payout = betAmount.value; // 타이면 베팅금 환불
      resultMessage.value = "타이 (베팅금 환불)";
    }
  }

  // 히스토리 추가
  history.value.unshift({ winner });
  if (history.value.length > 50) {
    history.value.pop();
  }

  // 잔액 업데이트
  winAmount.value = payout - betAmount.value;
  balance.value = balance.value - betAmount.value + payout;

  // 결과 표시
  setTimeout(() => {
    showResult.value = true;
    isPlaying.value = false;
  }, 1000);
};

const closeResult = () => {
  showResult.value = false;
};

const resetGame = () => {
  if (!isPlaying.value) {
    playerCards.value = [];
    bankerCards.value = [];
    playerScore.value = 0;
    bankerScore.value = 0;
    betAmount.value = 0;
    currentBet.value = null;
  }
};

const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));
</script>

<style scoped>
.baccarat-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  padding: 1rem;
  color: white;
}

.game-header {
  text-align: center;
  margin-bottom: 2rem;
}

.game-header h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.balance {
  background: rgba(255, 255, 255, 0.1);
  padding: 1rem;
  border-radius: 12px;
  display: inline-block;
  backdrop-filter: blur(10px);
}

.balance .label {
  opacity: 0.8;
  margin-right: 0.5rem;
}

.balance .amount {
  font-size: 1.5rem;
  font-weight: bold;
  color: #ffd700;
}

.balance .unit {
  margin-left: 0.3rem;
  opacity: 0.8;
}

/* 히스토리 */
.history-section {
  background: rgba(255, 255, 255, 0.1);
  padding: 1rem;
  border-radius: 12px;
  margin-bottom: 2rem;
  backdrop-filter: blur(10px);
}

.history-section h3 {
  margin-bottom: 0.5rem;
  font-size: 1.2rem;
}

.history-grid {
  display: flex;
  gap: 0.3rem;
  flex-wrap: wrap;
  max-height: 80px;
  overflow-y: auto;
}

.history-item {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.9rem;
}

.history-item.player {
  background: #4a90e2;
}

.history-item.banker {
  background: #e74c3c;
}

.history-item.tie {
  background: #27ae60;
}

/* 게임 테이블 */
.game-table {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 2rem;
}

.player-area,
.banker-area {
  background: rgba(255, 255, 255, 0.15);
  padding: 1.5rem;
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.player-area h3,
.banker-area h3 {
  text-align: center;
  margin-bottom: 1rem;
  font-size: 1.3rem;
}

.player-area h3 {
  color: #4a90e2;
}

.banker-area h3 {
  color: #e74c3c;
}

.cards {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
  min-height: 120px;
  align-items: center;
}

.card {
  width: 70px;
  height: 100px;
  background: white;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  animation: dealCard 0.3s ease-out;
}

@keyframes dealCard {
  from {
    transform: translateY(-50px) rotateY(180deg);
    opacity: 0;
  }
  to {
    transform: translateY(0) rotateY(0);
    opacity: 1;
  }
}

.card.hearts,
.card.diamonds {
  color: #e74c3c;
}

.card.clubs,
.card.spades {
  color: #2c3e50;
}

.card-value {
  font-size: 1.5rem;
  font-weight: bold;
}

.card-suit {
  font-size: 1.8rem;
}

.score {
  text-align: center;
  margin-top: 1rem;
  font-size: 1.2rem;
}

.score strong {
  font-size: 1.8rem;
  color: #ffd700;
}

/* 베팅 영역 */
.betting-section {
  background: rgba(255, 255, 255, 0.1);
  padding: 1.5rem;
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.bet-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.bet-option {
  background: rgba(255, 255, 255, 0.2);
  padding: 1.5rem;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 3px solid transparent;
}

.bet-option:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-3px);
}

.bet-option.active {
  border-color: #ffd700;
  background: rgba(255, 215, 0, 0.2);
}

.bet-title {
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.bet-odds {
  opacity: 0.8;
  font-size: 0.9rem;
}

.bet-amount-section h3 {
  margin-bottom: 1rem;
  font-size: 1.2rem;
}

.chip-buttons {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.chip-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 0.8rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s ease;
}

.chip-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.chip-btn:active {
  transform: translateY(0);
}

.chip-btn.reset {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.bet-input input {
  width: 100%;
  padding: 1rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 1.1rem;
  text-align: center;
}

.bet-input input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.bet-input input:disabled {
  opacity: 0.5;
}

.game-controls {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-top: 1.5rem;
}

.btn-primary,
.btn-secondary {
  padding: 1rem;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 결과 모달 */
.result-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.result-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
  border-radius: 16px;
  text-align: center;
  max-width: 400px;
  width: 90%;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(50px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.result-content h2 {
  font-size: 2rem;
  margin-bottom: 1.5rem;
}

.result-details {
  background: rgba(255, 255, 255, 0.1);
  padding: 1.5rem;
  border-radius: 12px;
  margin-bottom: 1.5rem;
}

.result-row {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
  font-size: 1.1rem;
}

.result-row.payout {
  border-top: 2px solid rgba(255, 255, 255, 0.3);
  margin-top: 0.5rem;
  padding-top: 1rem;
  font-size: 1.3rem;
}

.result-row .win {
  color: #2ecc71;
}

.result-row .lose {
  color: #e74c3c;
}

/* 반응형 */
@media (max-width: 768px) {
  .game-table {
    grid-template-columns: 1fr;
  }

  .bet-options {
    grid-template-columns: 1fr;
  }

  .chip-buttons {
    grid-template-columns: repeat(3, 1fr);
  }

  .card {
    width: 60px;
    height: 85px;
  }

  .card-value {
    font-size: 1.2rem;
  }

  .card-suit {
    font-size: 1.5rem;
  }
}
</style>
