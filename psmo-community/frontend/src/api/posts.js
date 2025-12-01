import apiClient from "./axios";

export const postsApi = {
  // 게시글 목록 조회
  getAll(params) {
    return apiClient.get("/api/posts", { params });
  },

  // 게시글 상세 조회
  getById(id) {
    return apiClient.get(`/api/posts/${id}`);
  },

  // 게시글 작성
  create(data) {
    return apiClient.post("/api/posts", data);
  },

  // 게시글 수정
  update(id, data) {
    return apiClient.put(`/api/posts/${id}`, data);
  },

  // 게시글 삭제
  delete(id) {
    return apiClient.delete(`/api/posts/${id}`);
  },

  // 내 게시글 조회
  getMyPosts() {
    return apiClient.get("/api/posts/my");
  },

  // 카테고리별 게시글 조회
  getByCategory(category, params) {
    return apiClient.get(`/api/posts/category/${category}`, { params });
  },

  // 게시글 좋아요
  like(id) {
    return apiClient.post(`/api/posts/${id}/like`);
  },

  // 게시글 좋아요 취소
  unlike(id) {
    return apiClient.delete(`/api/posts/${id}/like`);
  },
};
