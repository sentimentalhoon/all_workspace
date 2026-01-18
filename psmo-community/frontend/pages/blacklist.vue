<template>
  <div class="page-container">
    <div class="header-section">
      <h2 class="page-title">🚨 불량 사용자 블랙리스트</h2>
      <p class="page-desc">피해 사례를 공유하고 매장을 보호하세요.</p>
    </div>

    <!-- Glass Tabs -->
    <div class="glass-tabs">
      <button
        class="tab-btn"
        :class="{ active: activeTab === 'search' }"
        @click="activeTab = 'search'"
      >
        <span>🔍</span> 조회하기
      </button>
      <button
        class="tab-btn"
        :class="{ active: activeTab === 'report' }"
        @click="activeTab = 'report'"
      >
        <span>📝</span> 등록하기
      </button>
    </div>

    <!-- Search Tab Content -->
    <div v-show="activeTab === 'search'" class="tab-content fade-in">
      <div class="search-box glass-panel">
        <input
          v-model="searchKeyword"
          placeholder="이름 또는 전화번호 뒷 4자리를 입력하세요"
          class="dark-input"
          @keyup.enter="handleSearch"
        />
        <button
          class="search-btn"
          @click="handleSearch"
          :disabled="searchLoading"
        >
          <span v-if="searchLoading" class="spinner-sm"></span>
          <span v-else>조회</span>
        </button>
      </div>

      <div class="results-area">
        <div
          v-if="searched && searchResults.length === 0"
          class="no-results glass-panel"
        >
          <div class="icon">🤔</div>
          <p>검색 결과가 없습니다.</p>
          <p class="sub">
            등록된 불량 사용자가 아니거나, 정보가 일치하지 않습니다.
          </p>
        </div>

        <div v-else class="result-list">
          <div
            v-for="item in searchResults"
            :key="item.id"
            class="bad-user-card glass-panel"
          >
            <div class="card-header">
              <div class="user-main">
                <span class="bad-badge">주의</span>
                <span class="name">{{ item.name }}</span>
                <span class="phone">{{ item.phoneLast4 }}</span>
              </div>
              <span class="date">{{
                new Date(item.createdAt).toLocaleDateString()
              }}</span>
            </div>

            <div class="card-body">
              <p class="reason">“ {{ item.reason }} ”</p>

              <div
                v-if="item.imageUrls && item.imageUrls.length > 0"
                class="images-scroll"
              >
                <div
                  class="image-wrapper"
                  v-for="(url, idx) in item.imageUrls"
                  :key="idx"
                >
                  <img :src="url" alt="evidence" loading="lazy" />
                </div>
              </div>
            </div>

            <div class="card-footer">
              <span class="reporter">제보자: {{ item.reporterName }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Report Tab Content -->
    <div v-show="activeTab === 'report'" class="tab-content fade-in">
      <div class="report-form glass-panel">
        <div class="notice">
          <strong>⚠️ 주의사항</strong>
          <p>
            사실에 근거하지 않은 비방 목적의 글은 법적 책임이 따를 수
            있습니다.<br />
            정확한 피해 사실만 공유해주시기 바랍니다.
          </p>
        </div>

        <form @submit.prevent="handleReport">
          <div class="form-grid">
            <div class="form-group">
              <label>이름 <span class="required">*</span></label>
              <input
                v-model="reportForm.name"
                required
                placeholder="홍길동"
                class="dark-input"
              />
            </div>

            <div class="form-group">
              <label>전화번호 (전체) <span class="required">*</span></label>
              <input
                v-model="reportForm.phoneNumber"
                required
                placeholder="01012345678 (암호화 저장)"
                class="dark-input"
              />
              <small>검색 시에는 뒷 4자리만 노출됩니다.</small>
            </div>

            <div class="form-group">
              <label>출생년도 (선택)</label>
              <input
                type="number"
                v-model="reportForm.birthYear"
                placeholder="1990"
                class="dark-input"
              />
            </div>
          </div>

          <div class="form-group">
            <label>피해 사유 <span class="required">*</span></label>
            <textarea
              v-model="reportForm.reason"
              required
              placeholder="구체적인 피해 내용 (예: 야간 미성년자 출입 시도, 요금 미납 도주 등)"
              class="dark-input"
              rows="4"
            ></textarea>
          </div>

          <div class="form-group">
            <label>증거 사진 (선택)</label>
            <div class="file-upload-wrapper">
              <input
                type="file"
                multiple
                @change="handleFileChange"
                accept="image/*"
                id="file-input"
                class="file-input"
              />
              <label for="file-input" class="file-label">
                <span>📸 사진 선택 (최대 3장)</span>
                <span v-if="reportFiles.length > 0" class="file-count"
                  >{{ reportFiles.length }}장 선택됨</span
                >
              </label>
            </div>
          </div>

          <button type="submit" class="submit-btn" :disabled="reportLoading">
            <span v-if="reportLoading" class="spinner-sm white"></span>
            <span v-else>불량 사용자 등록</span>
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { searchBadUsers, reportBadUser } = useBlacklist();
const { user } = storeToRefs(useAuthStore());

const activeTab = ref<"search" | "report">("search");

// --- Search Logic ---
const searchKeyword = ref("");
const searchResults = ref<any[]>([]);
const searchLoading = ref(false);
const searched = ref(false);

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) return;
  searchLoading.value = true;
  searched.value = true;
  try {
    searchResults.value = await searchBadUsers(searchKeyword.value);
  } catch (e) {
    alert("검색 중 오류가 발생했습니다.");
  } finally {
    searchLoading.value = false;
  }
};

// --- Report Logic ---
const reportForm = ref({
  name: "",
  phoneNumber: "",
  birthYear: null as number | null,
  reason: "",
});
const reportFiles = ref<File[]>([]);
const reportLoading = ref(false);

const handleFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement;
  if (target.files) {
    reportFiles.value = Array.from(target.files);
  }
};

const handleReport = async () => {
  if (
    !confirm("허위 사실 유포 시 법적 책임을 질 수 있습니다. 등록하시겠습니까?")
  )
    return;

  reportLoading.value = true;
  try {
    await reportBadUser(reportForm.value, reportFiles.value);
    alert("성공적으로 등록되었습니다.");
    // Reset form
    reportForm.value = {
      name: "",
      phoneNumber: "",
      birthYear: null,
      reason: "",
    };
    reportFiles.value = [];
    activeTab.value = "search";

    // Auto Search to show result (Optimistic UX)
    searchKeyword.value = reportForm.value.name || "";
  } catch (e) {
    alert("등록 실패: " + e);
  } finally {
    reportLoading.value = false;
  }
};
</script>

<style scoped lang="scss">
/* --- Theme Variables --- */
$color-primary: #1e88e5;
$color-accent: #c5a059;
$color-danger: #e94560;
$glass-bg: rgba(255, 255, 255, 0.05);
$glass-border: rgba(255, 255, 255, 0.1);
$text-primary: #ffffff;
$text-secondary: #b0b0b0;

.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding-bottom: 40px;
  animation: fadeIn 0.5s ease;
}

.header-section {
  text-align: center;
  margin-bottom: 30px;

  .page-title {
    color: $text-primary;
    margin: 0 0 8px 0;
    font-size: 1.5rem;
  }

  .page-desc {
    color: $text-secondary;
    font-size: 0.95rem;
    margin: 0;
  }
}

/* --- Glass Tabs --- */
.glass-tabs {
  display: flex;
  background: rgba(0, 0, 0, 0.3);
  padding: 4px;
  border-radius: 12px;
  margin-bottom: 24px;

  .tab-btn {
    flex: 1;
    padding: 12px;
    border: none;
    background: transparent;
    color: $text-secondary;
    font-weight: 600;
    cursor: pointer;
    border-radius: 8px;
    transition: all 0.2s;
    font-size: 1rem;
    display: flex;
    justify-content: center;
    gap: 8px;

    &.active {
      background: rgba(255, 255, 255, 0.1);
      color: $color-accent;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
    }

    &:hover:not(.active) {
      color: white;
    }
  }
}

/* --- Search Box --- */
.glass-panel {
  background: $glass-bg;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid $glass-border;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.search-box {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  padding: 16px;

  .dark-input {
    flex: 1;
    background: rgba(0, 0, 0, 0.3);
    border: 1px solid $glass-border;
    color: white;
    padding: 14px;
    border-radius: 10px;
    font-size: 1rem;

    &:focus {
      outline: none;
      border-color: $color-primary;
      box-shadow: 0 0 0 2px rgba(30, 136, 229, 0.2);
    }
  }

  .search-btn {
    padding: 0 24px;
    background: $color-primary;
    color: white;
    border: none;
    border-radius: 10px;
    font-weight: bold;
    cursor: pointer;
    transition: background 0.2s;
    min-width: 80px;

    &:hover {
      background: lighten($color-primary, 10%);
    }
    &:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }
  }
}

/* --- Result List --- */
.no-results {
  text-align: center;
  padding: 60px 20px;
  color: $text-secondary;

  .icon {
    font-size: 3rem;
    margin-bottom: 16px;
  }
  p {
    margin: 0;
    font-size: 1.1rem;
  }
  .sub {
    font-size: 0.9rem;
    margin-top: 8px;
    opacity: 0.7;
  }
}

.bad-user-card {
  margin-bottom: 16px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 12px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);
    margin-bottom: 12px;

    .user-main {
      display: flex;
      align-items: center;
      gap: 10px;

      .bad-badge {
        background: rgba(233, 69, 96, 0.2);
        color: $color-danger;
        font-size: 0.75rem;
        padding: 4px 8px;
        border-radius: 4px;
        font-weight: bold;
      }

      .name {
        font-weight: bold;
        font-size: 1.1rem;
      }
      .phone {
        color: $text-secondary;
        font-size: 0.95rem;
      }
    }

    .date {
      font-size: 0.8rem;
      color: #666;
    }
  }

  .card-body {
    .reason {
      color: #ddd;
      line-height: 1.6;
      margin: 0 0 16px 0;
      font-style: italic;
      padding: 0 8px;
      border-left: 2px solid $color-danger;
    }

    .images-scroll {
      display: flex;
      gap: 10px;
      overflow-x: auto;
      padding-bottom: 4px;

      .image-wrapper {
        flex: 0 0 auto;

        img {
          width: 80px;
          height: 80px;
          object-fit: cover;
          border-radius: 8px;
          border: 1px solid rgba(255, 255, 255, 0.1);
          cursor: pointer;
        }
      }
    }
  }

  .card-footer {
    text-align: right;
    font-size: 0.8rem;
    color: #666;
    margin-top: 12px;
  }
}

/* --- Report Form --- */
.report-form {
  .notice {
    background: rgba(233, 69, 96, 0.1);
    border: 1px solid rgba(233, 69, 96, 0.3);
    color: #ff8a8a;
    padding: 16px;
    border-radius: 12px;
    margin-bottom: 24px;
    font-size: 0.9rem;
    line-height: 1.5;

    strong {
      display: block;
      margin-bottom: 6px;
      color: $color-danger;
    }
    p {
      margin: 0;
    }
  }

  .form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }

  .form-group {
    margin-bottom: 20px;
    display: flex;
    flex-direction: column;

    label {
      font-weight: 600;
      margin-bottom: 8px;
      color: $text-secondary;
      font-size: 0.9rem;

      .required {
        color: $color-danger;
        margin-left: 4px;
      }
    }

    .dark-input {
      background: rgba(0, 0, 0, 0.3);
      border: 1px solid $glass-border;
      color: white;
      padding: 12px;
      border-radius: 8px;
      font-size: 1rem;

      &:focus {
        outline: none;
        border-color: $color-primary;
      }
    }

    small {
      color: #666;
      margin-top: 6px;
      font-size: 0.8rem;
    }
  }

  .file-upload-wrapper {
    position: relative;

    .file-input {
      position: absolute;
      width: 0.1px;
      height: 0.1px;
      opacity: 0;
    }

    .file-label {
      display: flex;
      align-items: center;
      justify-content: space-between;
      background: rgba(255, 255, 255, 0.05);
      border: 1px dashed rgba(255, 255, 255, 0.2);
      padding: 12px 16px;
      border-radius: 8px;
      cursor: pointer;
      color: $text-secondary;
      transition: background 0.2s;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
      }

      .file-count {
        font-size: 0.8rem;
        color: $color-accent;
        font-weight: bold;
      }
    }
  }

  .submit-btn {
    width: 100%;
    padding: 16px;
    background: $color-danger;
    color: white;
    border: none;
    border-radius: 12px;
    font-weight: bold;
    font-size: 1.1rem;
    cursor: pointer;
    transition:
      transform 0.2s,
      background 0.2s;
    margin-top: 10px;

    &:hover {
      background: lighten($color-danger, 5%);
      transform: translateY(-2px);
    }

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
      transform: none;
    }
  }
}

/* --- Utilities --- */
.spinner-sm {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  display: inline-block;
}

@keyframes spin {
  100% {
    transform: rotate(360deg);
  }
}
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* Mobile Adjustments */
@media (max-width: 600px) {
  .report-form .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
