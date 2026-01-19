export interface BadUser {
  id: number;
  region: string;
  reason: string;
  physicalDescription: string | null;
  incidentDate: string | null;
  images: { id: number; url: string; thumbnailUrl: string }[];
  reporterName: string;
  reporterId: number;
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

  const fetchBadUserById = async (id: number): Promise<BadUser> => {
    return await fetchClient<BadUser>(`/blacklist/${id}`);
  };

  interface ImagePair {
    original: File;
    blurred: File | null;
  }

  const reportBadUser = async (
    data: BadUserCreateRequest,
    files: ImagePair[],
  ) => {
    const formData = new FormData();

    // JSON Data
    formData.append("data", JSON.stringify(data));

    // Files (최대 20장)
    files.forEach((pair, index) => {
      formData.append(`image_${index}`, pair.original);
      if (pair.blurred) {
        formData.append(`blur_image_${index}`, pair.blurred);
      }
    });

    return await fetchClient("/blacklist", {
      method: "POST",
      body: formData,
    });
  };

  const updateBadUser = async (
    id: number,
    data: BadUserCreateRequest,
    files: ImagePair[],
    deleteImageIds: number[],
  ) => {
    const formData = new FormData();
    formData.append("data", JSON.stringify(data));
    formData.append("deleteImageIds", JSON.stringify(deleteImageIds));

    files.forEach((pair, index) => {
      formData.append(`image_${index}`, pair.original);
      if (pair.blurred) {
        formData.append(`blur_image_${index}`, pair.blurred);
      }
    });

    return await fetchClient(`/blacklist/${id}`, {
      method: "PUT",
      body: formData,
    });
  };

  const deleteBadUser = async (id: number) => {
    return await fetchClient(`/blacklist/${id}`, {
      method: "DELETE",
    });
  };

  return {
    searchBadUsers,
    fetchBadUserById,
    reportBadUser,
    updateBadUser,
    deleteBadUser,
  };
};
