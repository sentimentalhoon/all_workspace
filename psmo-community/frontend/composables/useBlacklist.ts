export interface BadUser {
  id: number;
  region: string;
  reason: string;
  physicalDescription: string | null;
  incidentDate: string | null;
  images: { url: string; thumbnailUrl: string }[];
  reporterName: string;
  createdAt: string;
}

export interface BadUserCreateRequest {
  region: string;
  reason: string;
  physicalDescription?: string;
  incidentDate?: string;
}

export const useBlacklist = () => {
  const { fetchClient } = useApiClient();

  const searchBadUsers = async (keyword?: string): Promise<BadUser[]> => {
    return await fetchClient<BadUser[]>("/blacklist", {
      params: { keyword },
    });
  };

  const reportBadUser = async (data: BadUserCreateRequest, files: File[]) => {
    const formData = new FormData();

    // JSON Data
    formData.append("data", JSON.stringify(data));

    // Files (최대 20장)
    files.forEach((file) => {
      formData.append("images", file);
    });

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
