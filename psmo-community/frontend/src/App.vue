<template>
  <div id="app">
    <header>
      <div class="logo">PSMO Community</div>
      <nav>
        <router-link to="/">Home</router-link>
        <router-link to="/about">About</router-link>
      </nav>
    </header>
    <main>
      <router-view />
    </main>
    <footer>
      <p>&copy; 2025 PSMO Community</p>
    </footer>
  </div>
</template>

<script setup>
// App component
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  transition: all 0.3s;
  font-size: 0.9rem;
  text-align: center;
  flex: 1;
  max-width: 120px;
}

nav a:hover,
nav a:active {
  background-color: rgba(255, 255, 255, 0.2);
}

nav a.router-link-active {
  background-color: rgba(255, 255, 255, 0.3);
  font-weight: 600;
}

main {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
}

footer {
  background-color: #2c3e50;
  color: white;
  text-align: center;
  padding: 0.75rem;
  margin-top: auto;
  font-size: 0.875rem;
}

footer p {
  margin: 0;
}

/* 모바일 터치 최적화 */
@media (hover: none) and (pointer: coarse) {
  nav a:hover {
    background-color: transparent;
  }
}
</style>
