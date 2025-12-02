<template>
  <div class="board-view">
    <div class="page-header">
      <h2>💬 게시판</h2>
    </div>

    <BoardTabs v-model="activeTab" :tabs="boardTabs" />

    <div class="board-container">
      <BoardPostList
        v-if="activeTab === 'free'"
        :posts="freePosts"
        variant="free"
        write-label="✍️ 글쓰기"
      />

      <PcRoomList
        v-else-if="activeTab === 'pcroom'"
        :rooms="pcRooms"
        add-button-label="📍 피씨방 추가"
      />

      <BoardPostList
        v-else-if="activeTab === 'game'"
        :posts="gamePosts"
        variant="game"
        write-label="🎮 글쓰기"
      />

      <BoardPostList
        v-else-if="activeTab === 'tip'"
        :posts="tipPosts"
        variant="tip"
        write-label="💡 글쓰기"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * @fileoverview 게시판 메인 뷰로, 탭과 목록 구성을 담당하는 상위 페이지입니다.
 */
import BoardPostList from './board/components/BoardPostList.vue'
import BoardTabs from './board/components/BoardTabs.vue'
import PcRoomList from './board/components/PcRoomList.vue'
import { useBoardData } from './board/useBoardData'

/**
 * 게시판 탭별 데이터와 상호작용 함수를 공급하는 Composition API 훅입니다.
 * @see useBoardData
 */
const { activeTab, boardTabs, freePosts, pcRooms, gamePosts, tipPosts } = useBoardData()
</script>
<style src="./board/board-view.css"></style>
