import apiClient from "./axios";

export const commentsApi = {
  // 댓글 목록 조회 (게시글별)
  getByPost(postId, params) {
    return apiClient.get(`/api/posts/${postId}/comments`, { params });
  },

  // 댓글 작성
  create(postId, data) {
    return apiClient.post(`/api/posts/${postId}/comments`, data);
  },

  // 댓글 수정
  update(id, data) {
    return apiClient.put(`/api/comments/${id}`, data);
  },

  // 댓글 삭제
  delete(id) {
    return apiClient.delete(`/api/comments/${id}`);
  },

  // 내 댓글 목록 조회
  getMyComments() {
    return apiClient.get("/api/comments/my");
  },

  // 댓글 좋아요
  like(id) {
    return apiClient.post(`/api/comments/${id}/like`);
  },

  // 댓글 좋아요 취소
  unlike(id) {
    return apiClient.delete(`/api/comments/${id}/like`);
  },
};
