<template>
  <div class="report-view">
    <div class="page-header">
      <h2>⚠️ 진상 등록</h2>
      <p class="header-desc">피씨방 이용 시 발생한 진상 고객을 등록하고 공유합니다</p>
    </div>

    <div class="report-container">
      <div class="report-form">
        <h3>새 진상 등록</h3>

        <div class="form-group">
          <label>진상 유형 *</label>
          <div class="type-chips">
            <button
              v-for="type in reportTypes"
              :key="type"
              :class="{ active: selectedType === type }"
              @click="selectedType = type"
            >
              {{ type }}
            </button>
          </div>
        </div>

        <div class="form-group">
          <label>발생 지역 *</label>
          <select v-model="region">
            <option value="">선택하세요</option>
            <option value="서울">서울</option>
            <option value="경기">경기</option>
            <option value="인천">인천</option>
            <option value="강원">강원</option>
            <option value="충북">충북</option>
            <option value="충남">충남</option>
            <option value="대전">대전</option>
            <option value="경북">경북</option>
            <option value="경남">경남</option>
            <option value="대구">대구</option>
            <option value="울산">울산</option>
            <option value="부산">부산</option>
            <option value="전북">전북</option>
            <option value="전남">전남</option>
            <option value="광주">광주</option>
            <option value="제주">제주</option>
          </select>
        </div>

        <div class="form-group">
          <label>피씨방명</label>
          <input v-model="pcRoomName" type="text" placeholder="피씨방 이름" />
        </div>

        <div class="form-group">
          <label>발생 날짜 *</label>
          <input v-model="incidentDate" type="date" />
        </div>

        <div class="form-group">
          <label>진상 특징 (간략히)</label>
          <input v-model="characteristic" type="text" placeholder="예: 30대 남성, 검은색 모자" />
        </div>

        <div class="form-group">
          <label>상세 내용 *</label>
          <textarea
            v-model="content"
            placeholder="어떤 일이 있었는지 자세히 작성해주세요&#10;&#10;예시:&#10;- 음식물 쏟고 치우지 않음&#10;- 큰 소리로 욕설&#10;- 물건 파손 후 배상 거부&#10;- 성인물 시청 중 경고 무시"
            rows="8"
          ></textarea>
        </div>

        <div class="form-group">
          <label>심각도</label>
          <div class="severity-buttons">
            <button
              v-for="s in severityLevels"
              :key="s.value"
              :class="['severity-btn', s.class, { active: severity === s.value }]"
              @click="severity = s.value"
            >
              {{ s.label }}
            </button>
          </div>
        </div>

        <div class="form-actions">
          <button class="btn-submit" @click="submitReport">등록하기</button>
          <button class="btn-cancel" @click="resetForm">취소</button>
        </div>
      </div>

      <div class="recent-reports">
        <h3>최근 등록된 진상</h3>
        <div class="report-list">
          <div v-for="report in recentReports" :key="report.id" class="report-card">
            <div class="report-header">
              <span :class="['type-badge', report.typeClass]">{{ report.type }}</span>
              <span class="report-region">{{ report.region }}</span>
              <span class="report-date">{{ report.date }}</span>
            </div>
            <div class="report-info">
              <p class="pc-room-name" v-if="report.pcRoom">📍 {{ report.pcRoom }}</p>
              <p class="report-content">{{ report.content }}</p>
              <p class="report-characteristic" v-if="report.characteristic">
                👤 특징: {{ report.characteristic }}
              </p>
            </div>
            <div class="report-footer">
              <span :class="['severity-indicator', report.severityClass]">
                {{ report.severityLabel }}
              </span>
              <div class="report-stats">
                <span>👁️ {{ report.views }}</span>
                <span>💬 {{ report.comments }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const selectedType = ref('')
const region = ref('')
const pcRoomName = ref('')
const incidentDate = ref('')
const characteristic = ref('')
const content = ref('')
const severity = ref('medium')

const reportTypes = [
  '욕설/폭언',
  '기물파손',
  '불결/비위생',
  '성희롱',
  '소음',
  '음주',
  '금연구역 흡연',
  '미결제/먹튀',
  '기타',
]

const severityLevels = [
  { value: 'low', label: '경미', class: 'low' },
  { value: 'medium', label: '보통', class: 'medium' },
  { value: 'high', label: '심각', class: 'high' },
  { value: 'critical', label: '매우심각', class: 'critical' },
]

const recentReports = ref([
  {
    id: 1,
    type: '욕설/폭언',
    typeClass: 'type-abuse',
    region: '서울',
    pcRoom: '강남게임존',
    date: '2024-11-30',
    characteristic: '20대 남성, 빨간 점퍼',
    content:
      '게임 중 계속 큰 소리로 욕설. 다른 손님들 불편 호소했으나 무시. 직원 제지에도 계속 반복.',
    severityLabel: '심각',
    severityClass: 'severity-high',
    views: 245,
    comments: 12,
  },
  {
    id: 2,
    type: '불결/비위생',
    typeClass: 'type-dirty',
    region: '경기',
    pcRoom: '수원PC클럽',
    date: '2024-11-29',
    characteristic: '30대 남성',
    content: '라면 먹고 국물 자판에 쏟음. 치우지 않고 나가버림. 자판 고장.',
    severityLabel: '보통',
    severityClass: 'severity-medium',
    views: 189,
    comments: 8,
  },
  {
    id: 3,
    type: '미결제/먹튀',
    typeClass: 'type-payment',
    region: '인천',
    pcRoom: '부평게임타운',
    date: '2024-11-28',
    characteristic: '40대 남성, 검은 모자',
    content: '5시간 이용 후 화장실 간다며 나갔는데 돌아오지 않음. CCTV 확인 결과 도주.',
    severityLabel: '매우심각',
    severityClass: 'severity-critical',
    views: 512,
    comments: 34,
  },
])

const submitReport = () => {
  if (!selectedType.value || !region.value || !incidentDate.value || !content.value) {
    alert('필수 항목을 모두 입력해주세요.')
    return
  }
  console.log('Report submitted:', {
    type: selectedType.value,
    region: region.value,
    pcRoom: pcRoomName.value,
    date: incidentDate.value,
    characteristic: characteristic.value,
    content: content.value,
    severity: severity.value,
  })
  alert('진상 정보가 등록되었습니다.')
  resetForm()
}

const resetForm = () => {
  selectedType.value = ''
  region.value = ''
  pcRoomName.value = ''
  incidentDate.value = ''
  characteristic.value = ''
  content.value = ''
  severity.value = 'medium'
}
</script>

<style scoped>
.report-view {
  background: #f8f9fa;
  min-height: calc(100vh - 120px);
}

.page-header {
  background: white;
  padding: 1.5rem 1rem;
  border-bottom: 1px solid #e0e0e0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.page-header h2 {
  margin: 0 0 0.5rem 0;
  font-size: 1.5rem;
  color: #2d3748;
}

.header-desc {
  margin: 0;
  font-size: 0.85rem;
  color: #666;
}

.report-container {
  padding: 1rem;
}

.report-form,
.recent-reports {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  margin-bottom: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.report-form h3,
.recent-reports h3 {
  margin: 0 0 1.5rem 0;
  font-size: 1.1rem;
  color: #2d3748;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  font-weight: 600;
  color: #2d3748;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 0.95rem;
  font-family: inherit;
}

.form-group textarea {
  resize: vertical;
  line-height: 1.6;
}

.type-chips,
.severity-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.type-chips button {
  padding: 0.5rem 1rem;
  border: 1px solid #e0e0e0;
  background: white;
  border-radius: 20px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
}

.type-chips button:hover {
  background: #f5f5f5;
}

.type-chips button.active {
  background: #dc3545;
  color: white;
  border-color: #dc3545;
}

.severity-buttons {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0.5rem;
}

.severity-btn {
  padding: 0.75rem;
  border: 2px solid;
  background: white;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.severity-btn.low {
  border-color: #28a745;
  color: #28a745;
}

.severity-btn.medium {
  border-color: #ffc107;
  color: #f59e0b;
}

.severity-btn.high {
  border-color: #fd7e14;
  color: #fd7e14;
}

.severity-btn.critical {
  border-color: #dc3545;
  color: #dc3545;
}

.severity-btn.active.low {
  background: #28a745;
  color: white;
}

.severity-btn.active.medium {
  background: #ffc107;
  color: white;
}

.severity-btn.active.high {
  background: #fd7e14;
  color: white;
}

.severity-btn.active.critical {
  background: #dc3545;
  color: white;
}

.form-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
  margin-top: 2rem;
}

.btn-submit,
.btn-cancel {
  padding: 1rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-submit {
  background: #dc3545;
  color: white;
}

.btn-submit:hover {
  background: #c82333;
}

.btn-cancel {
  background: #f5f5f5;
  color: #666;
}

.btn-cancel:hover {
  background: #e0e0e0;
}

.report-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.report-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 1rem;
  background: #fafafa;
}

.report-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
  flex-wrap: wrap;
}

.type-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  color: white;
}

.type-abuse {
  background: #dc3545;
}

.type-dirty {
  background: #fd7e14;
}

.type-payment {
  background: #6f42c1;
}

.report-region {
  font-size: 0.85rem;
  color: #667eea;
  font-weight: 600;
}

.report-date {
  font-size: 0.8rem;
  color: #999;
  margin-left: auto;
}

.report-info {
  margin-bottom: 0.75rem;
}

.pc-room-name {
  margin: 0 0 0.5rem 0;
  font-size: 0.9rem;
  color: #667eea;
  font-weight: 600;
}

.report-content {
  margin: 0 0 0.5rem 0;
  color: #2d3748;
  font-size: 0.9rem;
  line-height: 1.6;
}

.report-characteristic {
  margin: 0;
  font-size: 0.85rem;
  color: #666;
  background: #fff;
  padding: 0.5rem;
  border-radius: 4px;
}

.report-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 0.75rem;
  border-top: 1px solid #e0e0e0;
}

.severity-indicator {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  color: white;
}

.severity-low {
  background: #28a745;
}

.severity-medium {
  background: #ffc107;
}

.severity-high {
  background: #fd7e14;
}

.severity-critical {
  background: #dc3545;
}

.report-stats {
  display: flex;
  gap: 1rem;
  font-size: 0.85rem;
  color: #666;
}
</style>
