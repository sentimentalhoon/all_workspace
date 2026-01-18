export interface BadUser {
  id: number;
  name: string;
  phoneLast4: string;
  birthYear: number | null;
  reason: string;
  imageUrls: string[];
  reporterName: string;
  createdAt: string;
}

export interface BadUserCreateRequest {
  name: string;
  phoneNumber: string; // Full phone number for hashing
  birthYear: number | null;
  reason: string;
}

export const useBlacklist = () => {
  const { fetchClient } = useApiClient();

  const searchBadUsers = async (keyword?: string) => {
    // API Call
    // GET /api/blacklist?keyword=...
    return await fetchClient<BadUser[]>("/blacklist", {
      params: { keyword },
    });
  };

  const reportBadUser = async (data: BadUserCreateRequest, files: File[]) => {
    // FormData Construction
    const formData = new FormData();

    // 1. JSON Data (as a string part, or individual fields? Ktor expects 'data' part for JSON)
    // The Backend Controller expects:
    // part.name == "data" -> JSON
    // part is FileItem -> File
    formData.append("data", JSON.stringify(data));

    // 2. Files
    files.forEach((file) => {
      formData.append("images", file);
    });

    // POST /api/blacklist
    // Note: When sending FormData with $fetch/axios, content-type is usually auto-set to multipart/form-data with boundary.
    // However, some custom fetch wrappers might need care. $fetch usually handles FormData.
    return await fetchClient("/blacklist", {
      method: "POST",
      body: formData,
    });
  };

  return {
    searchBadUsers,
    reportBadUser,
  };
};
