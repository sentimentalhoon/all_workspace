<template>
  <div class="about">
    <h1>About Campstation</h1>
    <p>캠핑장 예약 및 관리 시스템</p>
    <div class="tech-stack">
      <h2>기술 스택</h2>
      <div class="stack-section">
        <h3>Frontend</h3>
        <ul>
          <li>Vue.js 3</li>
          <li>Vue Router 4</li>
          <li>Pinia</li>
          <li>Vite</li>
          <li>Axios</li>
        </ul>
      </div>
      <div class="stack-section">
        <h3>Backend</h3>
        <ul>
          <li>Spring Boot 4.0.0</li>
          <li>Java 21</li>
          <li>Spring Data JPA</li>
          <li>MySQL / H2</li>
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
  max-width: 800px;
  margin: 0 auto;
}

h1 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.tech-stack {
  margin-top: 2rem;
}

.stack-section {
  background: #f5f5f5;
  padding: 1.5rem;
  margin: 1rem 0;
  border-radius: 8px;
}

.stack-section h3 {
  color: #42b983;
  margin-bottom: 1rem;
}

.stack-section ul {
  list-style: none;
  padding: 0;
}

.stack-section li {
  padding: 0.3rem 0;
  color: #2c3e50;
}
</style>
