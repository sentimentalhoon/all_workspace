<template>
  <div id="app">
    <header>
      <div class="logo">CampStation</div>
      <nav>
        <router-link to="/">Home</router-link>
        <router-link to="/about">About</router-link>
      </nav>
    </header>
    <main>
      <router-view />
    </main>
  </div>
</template>

<script setup>
// App component
</script>

<style scoped>
#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

header {
  background-color: #2c3e50;
  padding: 1rem;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.logo {
  color: white;
  font-size: 1.25rem;
  font-weight: bold;
  margin-bottom: 0.75rem;
  text-align: center;
}

nav {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
}

nav a {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s;
  font-size: 0.9rem;
  text-align: center;
  flex: 1;
  max-width: 120px;
}

nav a:hover,
nav a:active {
  background-color: #34495e;
}

nav a.router-link-active {
  background-color: #42b983;
}

main {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
}

/* 모바일 터치 최적화 */
@media (hover: none) and (pointer: coarse) {
  nav a:hover {
    background-color: transparent;
  }
}
</style>
