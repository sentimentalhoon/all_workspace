<template>
  <div class="about">
    <h1>About PSMO Community</h1>
    <p class="description">커뮤니티 기반 플랫폼</p>

    <div class="content-grid">
      <div class="tech-stack">
        <h2>🛠️ 기술 스택</h2>

        <div class="stack-section">
          <h3>Frontend</h3>
          <div class="tech-items">
            <span class="tech-badge">Vue.js 3</span>
            <span class="tech-badge">Vue Router 4</span>
            <span class="tech-badge">Pinia</span>
            <span class="tech-badge">Vite 6</span>
            <span class="tech-badge">Axios</span>
          </div>
        </div>

        <div class="stack-section">
          <h3>Backend</h3>
          <div class="tech-items">
            <span class="tech-badge">Ktor 3.0.2</span>
            <span class="tech-badge">Kotlin 2.1.0</span>
            <span class="tech-badge">Java 21</span>
            <span class="tech-badge">Exposed ORM</span>
            <span class="tech-badge">MySQL / H2</span>
          </div>
        </div>
      </div>

      <div class="features">
        <h2>✨ 주요 기능</h2>
        <ul>
          <li>사용자 인증 및 권한 관리</li>
          <li>커뮤니티 게시판</li>
          <li>실시간 알림</li>
          <li>프로필 관리</li>
          <li>검색 및 필터링</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
// About component
</script>

<style scoped>
.about {
  max-width: 900px;
  margin: 0 auto;
}

h1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
}

.description {
  color: #666;
  font-size: 1.1rem;
  margin-bottom: 2rem;
}

.content-grid {
  display: grid;
  gap: 2rem;
  margin-top: 2rem;
}

.tech-stack,
.features {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

h2 {
  color: #2c3e50;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
}

.stack-section {
  margin-bottom: 1.5rem;
}

.stack-section:last-child {
  margin-bottom: 0;
}

.stack-section h3 {
  color: #667eea;
  margin-bottom: 1rem;
  font-size: 1.2rem;
}

.tech-items {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tech-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.features ul {
  list-style: none;
  padding: 0;
}

.features li {
  padding: 0.75rem 0;
  color: #2c3e50;
  border-bottom: 1px solid #eee;
  position: relative;
  padding-left: 1.5rem;
}

.features li:before {
  content: "✓";
  position: absolute;
  left: 0;
  color: #42b983;
  font-weight: bold;
}

.features li:last-child {
  border-bottom: none;
}

@media (min-width: 768px) {
  .content-grid {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
