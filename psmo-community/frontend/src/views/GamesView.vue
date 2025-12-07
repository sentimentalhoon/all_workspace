<template>
  <div class="games-view">
    <header class="games-hero">
      <div class="hero-text">
        <p class="eyebrow">포인트 플레이</p>
        <h1>가볍게 즐기는 커뮤니티 게임</h1>
        <p class="lead">
          모은 포인트로 뽑기, 룰렛, 퀴즈 등 미니게임을 즐겨보세요. 플레이 기록과 확률은 투명하게
          공개됩니다.
        </p>
      </div>
      <div class="hero-actions">
        <RouterLink to="/games" class="cta primary">🎮 지금 플레이</RouterLink>
        <RouterLink to="/my" class="cta ghost">내 포인트 확인</RouterLink>
      </div>
    </header>

    <section class="games-grid">
      <article class="game-card highlight">
        <div class="pill">신규</div>
        <h3>달팽이 경주</h3>
        <p>3마리 달팽이 중 우승을 맞추는 HTML5 캔버스 실시간 레이스.</p>
        <div class="meta">
          <span>실시간 애니메이션</span>
          <span>포인트 베팅 연동 예정</span>
        </div>
        <RouterLink to="/games/snail" class="play-btn">경주장 입장</RouterLink>
      </article>

      <article class="game-card">
        <div class="pill">저포인트</div>
        <h3>데일리 룰렛</h3>
        <p>하루 1회 무료, 이후 소액 포인트로 즉시 보상 추첨.</p>
        <div class="meta">
          <span>쿨다운 6h</span>
          <span>보상: 배지/포인트</span>
        </div>
        <button class="play-btn" type="button">플레이(준비중)</button>
      </article>

      <article class="game-card">
        <div class="pill">협동</div>
        <h3>커뮤니티 미션</h3>
        <p>모두가 포인트를 합쳐 목표 달성 시 전원에게 특별 보상 지급.</p>
        <div class="meta">
          <span>주간 진행</span>
          <span>투명 로그</span>
        </div>
        <button class="play-btn" type="button">입장(준비중)</button>
      </article>

      <article class="game-card">
        <div class="pill">경매</div>
        <h3>포인트 경매</h3>
        <p>희소 배지/테마를 포인트로 경매. 낙찰자는 코스메틱 보상 확보.</p>
        <div class="meta">
          <span>실시간 업데이트</span>
          <span>환불 자동 처리</span>
        </div>
        <button class="play-btn" type="button">라운드 보기</button>
      </article>

      <article class="game-card">
        <div class="pill">퀴즈</div>
        <h3>데일리 퀴즈</h3>
        <p>포인트로 입장해 상위 정답자에게 보상을 분배. 부정 방지 쿨다운 적용.</p>
        <div class="meta">
          <span>일일 1회</span>
          <span>상위 10% 보상</span>
        </div>
        <button class="play-btn" type="button">퀴즈 풀기</button>
      </article>
    </section>

    <section class="safety">
      <h4>공정성 & 안전장치</h4>
      <ul>
        <li>확률·배당 공개 및 서버 로그 기록</li>
        <li>일일/주간 참여 한도와 쿨다운 적용</li>
        <li>포인트는 현금성 없음, 코스메틱·명예 보상 중심</li>
        <li>봇/다중 계정 방지: 캡차, 기기/세션 제한</li>
      </ul>
    </section>
  </div>
</template>

<script setup lang="ts">
// 미니게임 허브 뷰. 실제 게임별 화면은 추후 /games/* 하위 라우트로 분리 예정.
</script>

<style scoped>
.games-view {
  padding: 1.5rem 1rem 2.5rem;
  background: #f8f9fb;
  min-height: calc(100vh - 60px);
}

.games-hero {
  background:
    radial-gradient(circle at 20% 20%, rgba(102, 126, 234, 0.18), transparent 45%),
    radial-gradient(circle at 80% 10%, rgba(86, 204, 242, 0.18), transparent 40%), #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
  margin-bottom: 1.25rem;
}

.hero-text .eyebrow {
  margin: 0;
  font-size: 0.85rem;
  font-weight: 700;
  color: #6366f1;
}

.hero-text h1 {
  margin: 0.35rem 0 0.4rem;
  font-size: 1.35rem;
  color: #0f172a;
}

.hero-text .lead {
  margin: 0;
  color: #475569;
  line-height: 1.5;
  font-size: 0.95rem;
}

.hero-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 1rem;
  flex-wrap: wrap;
}

.cta {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.75rem 1.1rem;
  border-radius: 10px;
  font-weight: 700;
  text-decoration: none;
  border: 1px solid transparent;
  transition:
    transform 0.15s ease,
    box-shadow 0.15s ease;
}

.cta.primary {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
  box-shadow: 0 8px 18px rgba(99, 102, 241, 0.22);
}

.cta.ghost {
  background: #fff;
  color: #4b5563;
  border-color: #e5e7eb;
}

.cta:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.06);
}

.games-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 0.9rem;
  margin-bottom: 1.25rem;
}

.game-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 1rem;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  display: grid;
  gap: 0.4rem;
}

.game-card.highlight {
  border-color: #c7d2fe;
  box-shadow: 0 10px 28px rgba(99, 102, 241, 0.1);
}

.game-card h3 {
  margin: 0;
  font-size: 1.05rem;
  color: #111827;
}

.game-card p {
  margin: 0;
  color: #4b5563;
  line-height: 1.4;
}

.pill {
  display: inline-flex;
  align-items: center;
  padding: 0.25rem 0.6rem;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 700;
  background: #eef2ff;
  color: #4338ca;
  width: fit-content;
}

.meta {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  font-size: 0.85rem;
  color: #6b7280;
}

.play-btn {
  margin-top: 0.25rem;
  padding: 0.65rem 0.9rem;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  background: #f8fafc;
  color: #1f2937;
  font-weight: 700;
  cursor: pointer;
  transition:
    background 0.15s ease,
    border-color 0.15s ease;
  text-decoration: none;
  text-align: center;
}

.play-btn:hover {
  background: #eef2ff;
  border-color: #c7d2fe;
}

.safety {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 1rem;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
}

.safety h4 {
  margin: 0 0 0.5rem;
  color: #111827;
}

.safety ul {
  margin: 0;
  padding-left: 1.1rem;
  color: #4b5563;
  line-height: 1.5;
}

@media (min-width: 720px) {
  .games-view {
    padding: 2rem;
    max-width: 960px;
    margin: 0 auto;
  }

  .games-hero {
    display: grid;
    grid-template-columns: 2fr 1fr;
    align-items: center;
    gap: 1rem;
  }
}
</style>
