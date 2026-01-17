<!--
  Nuxt 애플리케이션의 최상위 컴포넌트입니다.
  모든 페이지는 이 파일 안에서 그려집니다.

  <NuxtLayout>: layouts/default.vue 같은 레이아웃을 감싸줍니다.
  <NuxtPage>: pages/ 폴더 안에 있는 실제 페이지(화면)를 여기에 보여줍니다.
-->
<template>
  <div>
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </div>
</template>
