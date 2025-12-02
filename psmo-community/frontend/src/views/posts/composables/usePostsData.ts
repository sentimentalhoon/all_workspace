import type {
  CommentActionPayload,
  MyComment,
  MyPost,
  PostActionPayload,
  SavedActionPayload,
  SavedPost,
  TabId,
} from '@/views/posts/constants'
import {
  initialMyComments,
  initialMyPosts,
  initialSavedPosts,
  postTabs,
} from '@/views/posts/constants'
import { ref } from 'vue'

export function usePostsData() {
  const activeTab = ref<TabId>('posts')
  const tabs = postTabs

  const myPosts = ref<MyPost[]>([...initialMyPosts])
  const myComments = ref<MyComment[]>([...initialMyComments])
  const savedPosts = ref<SavedPost[]>([...initialSavedPosts])

  const handlePostAction = (payload: PostActionPayload) => {
    console.info('Post action (placeholder)', payload)
  }

  const handleCommentAction = (payload: CommentActionPayload) => {
    console.info('Comment action (placeholder)', payload)
  }

  const handleSavedAction = (payload: SavedActionPayload) => {
    console.info('Saved post action (placeholder)', payload)
  }

  return {
    activeTab,
    tabs,
    myPosts,
    myComments,
    savedPosts,
    handlePostAction,
    handleCommentAction,
    handleSavedAction,
  }
}
