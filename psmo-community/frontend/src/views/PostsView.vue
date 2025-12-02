<template>
  <div class="posts-view">
    <div class="page-header">
      <h2>✍️ 내가 쓴 글</h2>
    </div>

    <PostsTabs v-model="activeTab" :tabs="tabs" />

    <div class="posts-container">
      <MyPostsList v-if="activeTab === 'posts'" :posts="myPosts" @action="handlePostAction" />

      <MyCommentsList
        v-else-if="activeTab === 'comments'"
        :comments="myComments"
        @action="handleCommentAction"
      />

      <SavedPostsList v-else :posts="savedPosts" @action="handleSavedAction" />
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * @fileoverview 내가 쓴 글/댓글/스크랩을 탭으로 전환해 보여주는 페이지입니다.
 */
import MyCommentsList from '@/views/posts/components/MyCommentsList.vue'
import MyPostsList from '@/views/posts/components/MyPostsList.vue'
import PostsTabs from '@/views/posts/components/PostsTabs.vue'
import SavedPostsList from '@/views/posts/components/SavedPostsList.vue'
import { usePostsData } from '@/views/posts/composables/usePostsData'

/**
 * 게시글, 댓글, 저장한 글 목록과 각 액션 핸들러를 제공하는 훅입니다.
 * @see usePostsData
 */
const {
  activeTab,
  tabs,
  myPosts,
  myComments,
  savedPosts,
  handlePostAction,
  handleCommentAction,
  handleSavedAction,
} = usePostsData()
</script>

<style src="@/views/posts/styles/posts-view.css"></style>
