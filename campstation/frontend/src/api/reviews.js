import apiClient from "./axios";

export const reviewsApi = {
  // 리뷰 목록 조회 (캠핑장별)
  getByCampsite(campsiteId, params) {
    return apiClient.get(`/api/campsites/${campsiteId}/reviews`, { params });
  },

  // 리뷰 작성
  create(campsiteId, data) {
    return apiClient.post(`/api/campsites/${campsiteId}/reviews`, data);
  },

  // 내 리뷰 목록 조회
  getMyReviews() {
    return apiClient.get("/api/reviews/my");
  },

  // 리뷰 수정
  update(id, data) {
    return apiClient.put(`/api/reviews/${id}`, data);
  },

  // 리뷰 삭제
  delete(id) {
    return apiClient.delete(`/api/reviews/${id}`);
  },
};
