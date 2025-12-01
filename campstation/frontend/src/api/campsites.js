import apiClient from "./axios";

export const campsitesApi = {
  // 캠핑장 목록 조회
  getAll(params) {
    return apiClient.get("/api/campsites", { params });
  },

  // 캠핑장 상세 조회
  getById(id) {
    return apiClient.get(`/api/campsites/${id}`);
  },

  // 캠핑장 검색
  search(keyword, params) {
    return apiClient.get("/api/campsites/search", {
      params: { keyword, ...params },
    });
  },

  // 캠핑장 찜하기
  addToFavorites(id) {
    return apiClient.post(`/api/campsites/${id}/favorite`);
  },

  // 캠핑장 찜 취소
  removeFromFavorites(id) {
    return apiClient.delete(`/api/campsites/${id}/favorite`);
  },

  // 내 찜 목록 조회
  getMyFavorites() {
    return apiClient.get("/api/campsites/favorites");
  },
};
