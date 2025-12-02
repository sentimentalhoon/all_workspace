<template>
  <div class="report-view">
    <div class="page-header">
      <h2>⚠️ 진상 등록</h2>
      <p class="header-desc">피씨방 이용 시 발생한 진상 고객을 등록하고 공유합니다</p>
    </div>

    <div class="report-container">
      <ReportForm
        v-model:selected-type="selectedType"
        v-model:region="region"
        v-model:pc-room-name="pcRoomName"
        v-model:incident-date="incidentDate"
        v-model:characteristic="characteristic"
        v-model:content="content"
        v-model:severity="severity"
        :report-types="reportTypes"
        :severity-levels="severityLevels"
        :max-photos="MAX_PHOTOS"
        :max-file-size-mb="MAX_FILE_SIZE_MB"
        :photo-previews="photoPreviews"
        :is-optimizing-photos="isOptimizingPhotos"
        @photo-upload="handlePhotoUpload"
        @remove-photo="removePhoto"
        @submit="submitReport"
        @reset="resetForm"
      />

      <RecentReports :reports="recentReports" />
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * @fileoverview 진상 신고 작성 폼과 최근 신고 리스트를 통합 관리하는 상위 페이지입니다.
 */
import RecentReports from './report/components/RecentReports.vue'
import ReportForm from './report/components/ReportForm.vue'
import { usePhotoUpload } from './report/usePhotoUpload'
import { useReportForm } from './report/useReportForm'

/**
 * 사진 업로드 수와 용량을 제어하는 Composition API 훅 인스턴스입니다.
 * @see usePhotoUpload
 */
const photoUpload = usePhotoUpload()

/**
 * 신고 폼 입력 상태 및 제출 로직을 캡슐화한 훅에서 필요한 값만 구조 분해합니다.
 * @see useReportForm
 */
const {
  selectedType,
  region,
  pcRoomName,
  incidentDate,
  characteristic,
  content,
  severity,
  reportTypes,
  severityLevels,
  recentReports,
  submitReport,
  resetForm,
} = useReportForm({
  getPhotoCount: () => photoUpload.photos.value.length,
  clearPhotos: photoUpload.clearPhotos,
})

const {
  photoPreviews,
  isOptimizingPhotos,
  handlePhotoUpload,
  removePhoto,
  MAX_PHOTOS,
  MAX_FILE_SIZE_MB,
} = photoUpload
</script>
<style src="./report/report-view.css"></style>
