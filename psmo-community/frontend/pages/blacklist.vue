<template>
  <div class="page-container">
    <div class="header-section">
      <h2>🚨 불량 사용자 블랙리스트</h2>
      <p>피해 사례를 공유하고 예방하세요.</p>
    </div>

    <div class="tabs">
      <button
        :class="{ active: activeTab === 'search' }"
        @click="activeTab = 'search'"
      >
        🔍 조회하기
      </button>
      <button
        :class="{ active: activeTab === 'report' }"
        @click="activeTab = 'report'"
      >
        📝 등록하기
      </button>
    </div>

    <!-- Search Tab -->
    <div v-if="activeTab === 'search'" class="tab-content">
      <div class="search-box">
        <input
          v-model="searchKeyword"
          placeholder="이름 또는 전화번호 뒷 4자리 입력"
          @keyup.enter="handleSearch"
        />
        <button
          class="search-btn"
          @click="handleSearch"
          :disabled="searchLoading"
        >
          {{ searchLoading ? "검색 중..." : "조회" }}
        </button>
      </div>

      <div class="results-area">
        <div v-if="searched && searchResults.length === 0" class="no-results">
          <p>검색 결과가 없습니다.</p>
          <p class="sub">
            등록된 불량 사용자가 아니거나, 정보가 일치하지 않습니다.
          </p>
        </div>

        <div v-else class="result-list">
          <div
            v-for="item in searchResults"
            :key="item.id"
            class="bad-user-card"
          >
            <div class="card-header">
              <span class="name">{{ item.name }}</span>
              <span class="phone">({{ item.phoneLast4 }})</span>
              <span class="date">{{
                new Date(item.createdAt).toLocaleDateString()
              }}</span>
            </div>
            <div class="card-body">
              <p class="reason">{{ item.reason }}</p>
              <div
                v-if="item.imageUrls && item.imageUrls.length > 0"
                class="images"
              >
                <img
                  v-for="(url, idx) in item.imageUrls"
                  :key="idx"
                  :src="url"
                  alt="evidence"
                />
              </div>
            </div>
            <div class="card-footer">
              <span class="reporter">제보자: {{ item.reporterName }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Report Tab -->
    <div v-if="activeTab === 'report'" class="tab-content">
      <div class="report-form card">
        <div class="notice">
          <strong>⚠️ 주의사항</strong>
          <p>
            사실에 근거하지 않은 비방 목적의 글은 삭제될 수 있으며, 법적 책임은
            작성자에게 있습니다.
          </p>
        </div>

        <form @submit.prevent="handleReport">
          <div class="form-group">
            <label>이름</label>
            <input v-model="reportForm.name" required placeholder="홍길동" />
          </div>

          <div class="form-group">
            <label>전화번호 (전체 입력)</label>
            <input
              v-model="reportForm.phoneNumber"
              required
              placeholder="01012345678 (저장은 암호화)"
            />
            <small
              >검색 시에는 뒷 4자리만, 검증 시에는 전체 번호가
              사용됩니다.</small
            >
          </div>

          <div class="form-group">
            <label>출생년도 (선택)</label>
            <input
              type="number"
              v-model="reportForm.birthYear"
              placeholder="예: 1990"
            />
          </div>

          <div class="form-group">
            <label>피해 사유</label>
            <textarea
              v-model="reportForm.reason"
              required
              placeholder="구체적인 피해 내용을 작성해주세요 (먹튀, 기물 파손 등)"
            ></textarea>
          </div>

          <div class="form-group">
            <label>증거 사진 (선택)</label>
            <input
              type="file"
              multiple
              @change="handleFileChange"
              accept="image/*"
            />
          </div>

          <button type="submit" class="submit-btn" :disabled="reportLoading">
            {{ reportLoading ? "등록 중..." : "불량 사용자 등록" }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { searchBadUsers, reportBadUser } = useBlacklist();
const { user } = storeToRefs(useAuthStore()); // Ensure user is logged in

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
    alert("등록되었습니다.");
    // Reset form
    reportForm.value = {
      name: "",
      phoneNumber: "",
      birthYear: null,
      reason: "",
    };
    reportFiles.value = [];
    activeTab.value = "search";
    // Optional: Auto search the reported user?
  } catch (e) {
    alert("등록 실패: " + e);
  } finally {
    reportLoading.value = false;
  }
};
</script>

<style scoped>
.page-container {
  padding: 10px;
  max-width: 800px;
  margin: 0 auto;
}

.header-section {
  text-align: center;
  margin-bottom: 20px;
}

.header-section h2 {
  color: #e94560;
  margin-bottom: 5px;
}

.header-section p {
  color: #888;
  font-size: 0.9rem;
}

.tabs {
  display: flex;
  margin-bottom: 20px;
  background: white;
  border-radius: 12px;
  padding: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.tabs button {
  flex: 1;
  padding: 12px;
  border: none;
  background: transparent;
  font-weight: bold;
  color: #888;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
}

.tabs button.active {
  background: #16213e;
  color: #c5a059;
}

.search-box {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-box input {
  flex: 1;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
}

.search-btn {
  padding: 0 25px;
  background: #16213e;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
}

.no-results {
  text-align: center;
  padding: 50px 0;
  color: #888;
}

.card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.bad-user-card {
  background: white;
  margin-bottom: 15px;
  border-radius: 12px;
  padding: 15px;
  border: 1px solid #eee;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 10px;
}

.name {
  font-weight: bold;
  font-size: 1.1rem;
  color: #333;
}
.phone {
  color: #e94560;
  font-weight: bold;
}
.date {
  margin-left: auto;
  font-size: 0.8rem;
  color: #aaa;
}

.card-body .reason {
  color: #555;
  line-height: 1.5;
  margin-bottom: 15px;
}

.images {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 5px;
}

.images img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #eee;
}

.card-footer {
  margin-top: 10px;
  text-align: right;
  font-size: 0.8rem;
  color: #999;
}

.notice {
  background: #fff5f5;
  color: #e53e3e;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 0.9rem;
}

.form-group {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.form-group input,
.form-group textarea {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.form-group small {
  color: #888;
  margin-top: 5px;
  font-size: 0.8rem;
}

.submit-btn {
  width: 100%;
  padding: 15px;
  background: #e94560;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 10px;
}

.submit-btn:disabled {
  background: #ccc;
}
</style>
