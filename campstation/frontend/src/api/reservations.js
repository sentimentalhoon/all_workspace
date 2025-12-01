import apiClient from "./axios";

export const reservationsApi = {
  // 예약 생성
  create(campsiteId, data) {
    return apiClient.post(`/api/reservations`, { campsiteId, ...data });
  },

  // 내 예약 목록 조회
  getMyReservations() {
    return apiClient.get("/api/reservations/my");
  },

  // 예약 상세 조회
  getById(id) {
    return apiClient.get(`/api/reservations/${id}`);
  },

  // 예약 취소
  cancel(id) {
    return apiClient.delete(`/api/reservations/${id}`);
  },

  // 예약 수정
  update(id, data) {
    return apiClient.put(`/api/reservations/${id}`, data);
  },
};
