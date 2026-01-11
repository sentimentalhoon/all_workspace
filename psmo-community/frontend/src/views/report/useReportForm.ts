import { ref, watch } from 'vue'

export type SeverityLevel = {
  value: string
  label: string
  class: string
}

interface UseReportFormOptions {
  getPhotoCount: () => number
  clearPhotos: () => void
}

export const useReportForm = ({ getPhotoCount, clearPhotos }: UseReportFormOptions) => {
  const selectedType = ref('')
  const region = ref('')
  const pcRoomName = ref('')
  const incidentDate = ref('')
  const characteristic = ref('')
  const content = ref('')
  const severity = ref('medium')
  const hashtags = ref<string[]>([])
  
  // Auto-generate hashtags based on structured input
  watch(
    [selectedType, region, pcRoomName],
    ([newType, newRegion, newPcRoom], [oldType, oldRegion, oldPcRoom]) => {
      let tags = [...hashtags.value]

      // Helper to replace tag
      const replaceTag = (oldVal: string, newVal: string) => {
        if (!oldVal) {
             if (newVal && !tags.includes(newVal)) tags.push(newVal)
             return
        }
        const idx = tags.indexOf(oldVal)
        if (idx !== -1) {
            if (newVal) tags[idx] = newVal
            else tags.splice(idx, 1) // Remove if new value is empty
        } else {
            if (newVal && !tags.includes(newVal)) tags.push(newVal)
        }
      }
      
      // Update Type Tag
      replaceTag(oldType, newType)
      
      // Update Region Tag
      replaceTag(oldRegion, newRegion)
      
      // Update PCRoom Tag (remove spaces for hashtag)
      const fmtPc = (s: string) => s.trim().replace(/\s+/g, '')
      replaceTag(fmtPc(oldPcRoom), fmtPc(newPcRoom))
      
      hashtags.value = tags
    }
  )

  const reportTypes = [
    // ... existing report types
  ]
// ... existing rest of file
  value: string
  label: string
  class: string
}

interface UseReportFormOptions {
  getPhotoCount: () => number
  clearPhotos: () => void
}

export const useReportForm = ({ getPhotoCount, clearPhotos }: UseReportFormOptions) => {
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

  const severityLevels: SeverityLevel[] = [
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

  const photoCount = getPhotoCount() // Keep this for photo count

  const videoFile = ref<File | null>(null)

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
      photoCount: getPhotoCount(),
      videoFile: videoFile.value ? videoFile.value.name : null,
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
    videoFile.value = null
    hashtags.value = []
    clearPhotos()
  }

  const handleVideoUpload = (file: File) => {
    videoFile.value = file
  }

  const handleVideoRemove = () => {
    videoFile.value = null
  }

  return {
    selectedType,
    region,
    pcRoomName,
    incidentDate,
    characteristic,
    content,
    severity,
    hashtags,
    reportTypes,
    severityLevels,
    recentReports,
    submitReport,
    resetForm,
    videoFile,
    handleVideoUpload,
    handleVideoRemove,
  }
}
