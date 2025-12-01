<script setup lang="ts">
import { ref } from 'vue'

const activeTab = ref('upcoming')

const upcomingBookings = [
  {
    id: 1,
    campingName: '다문188',
    location: '경기 양평군',
    checkIn: '2025-12-15',
    checkOut: '2025-12-16',
    status: '예약확정',
  },
]

const pastBookings = [
  {
    id: 2,
    campingName: '골짜구니 캠핑장',
    location: '강원 양양군',
    checkIn: '2025-11-20',
    checkOut: '2025-11-21',
    status: '이용완료',
  },
]
</script>

<template>
  <div class="bookings-page">
    <header class="page-header">
      <h1>예약 내역</h1>
    </header>

    <div class="tabs">
      <button
        :class="['tab', { active: activeTab === 'upcoming' }]"
        @click="activeTab = 'upcoming'"
      >
        예정된 예약
      </button>
      <button :class="['tab', { active: activeTab === 'past' }]" @click="activeTab = 'past'">
        지난 예약
      </button>
    </div>

    <div class="bookings-container">
      <div v-if="activeTab === 'upcoming'">
        <div v-if="upcomingBookings.length === 0" class="empty-state">
          <div class="empty-icon">📅</div>
          <p>예정된 예약이 없습니다</p>
        </div>
        <div v-else class="booking-list">
          <div v-for="booking in upcomingBookings" :key="booking.id" class="booking-card">
            <div class="booking-status confirmed">{{ booking.status }}</div>
            <h3>{{ booking.campingName }}</h3>
            <p class="location">📍 {{ booking.location }}</p>
            <div class="booking-dates">
              <div class="date-info">
                <span class="label">체크인</span>
                <span class="date">{{ booking.checkIn }}</span>
              </div>
              <div class="date-info">
                <span class="label">체크아웃</span>
                <span class="date">{{ booking.checkOut }}</span>
              </div>
            </div>
            <div class="booking-actions">
              <button class="btn-secondary">예약 상세</button>
              <button class="btn-primary">길 찾기</button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'past'">
        <div v-if="pastBookings.length === 0" class="empty-state">
          <div class="empty-icon">📋</div>
          <p>지난 예약 내역이 없습니다</p>
        </div>
        <div v-else class="booking-list">
          <div v-for="booking in pastBookings" :key="booking.id" class="booking-card">
            <div class="booking-status completed">{{ booking.status }}</div>
            <h3>{{ booking.campingName }}</h3>
            <p class="location">📍 {{ booking.location }}</p>
            <div class="booking-dates">
              <div class="date-info">
                <span class="label">체크인</span>
                <span class="date">{{ booking.checkIn }}</span>
              </div>
              <div class="date-info">
                <span class="label">체크아웃</span>
                <span class="date">{{ booking.checkOut }}</span>
              </div>
            </div>
            <div class="booking-actions">
              <button class="btn-secondary">예약 상세</button>
              <button class="btn-primary">후기 작성</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.bookings-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.page-header {
  background: white;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e9ecef;
  position: sticky;
  top: 0;
  z-index: 10;
}

.page-header h1 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 700;
  color: #2d3748;
}

.tabs {
  display: flex;
  background: white;
  border-bottom: 1px solid #e9ecef;
}

.tab {
  flex: 1;
  padding: 1rem;
  border: none;
  background: none;
  color: #868e96;
  font-weight: 600;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
}

.tab.active {
  color: #667eea;
  border-bottom-color: #667eea;
}

.bookings-container {
  padding: 1rem;
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #868e96;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.empty-state p {
  margin: 0;
  font-size: 1rem;
}

.booking-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.booking-card {
  background: white;
  border-radius: 12px;
  padding: 1.25rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.booking-status {
  display: inline-block;
  padding: 0.375rem 0.875rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
}

.booking-status.confirmed {
  background: #e3f2fd;
  color: #1976d2;
}

.booking-status.completed {
  background: #f1f3f5;
  color: #495057;
}

.booking-card h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.125rem;
  color: #2d3748;
}

.location {
  margin: 0 0 1rem 0;
  color: #868e96;
  font-size: 0.9rem;
}

.booking-dates {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 1rem;
}

.date-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.date-info .label {
  font-size: 0.8rem;
  color: #868e96;
}

.date-info .date {
  font-size: 0.95rem;
  font-weight: 600;
  color: #2d3748;
}

.booking-actions {
  display: flex;
  gap: 0.75rem;
}

.btn-secondary,
.btn-primary {
  flex: 1;
  padding: 0.75rem;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary {
  background: #f8f9fa;
  color: #495057;
}

.btn-secondary:hover {
  background: #e9ecef;
}

.btn-primary {
  background: #667eea;
  color: white;
}

.btn-primary:hover {
  background: #5a67d8;
}
</style>
