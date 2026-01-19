export interface Banner {
  id: number;
  title: string;
  imageUrl: string;
  linkUrl: string | null;
  isVisible: boolean;
  orderIndex: number;
  createdAt: string;
}

export interface BannerCreateRequest {
  title: string;
  linkUrl?: string;
  isVisible: boolean;
  orderIndex: number;
}

export const useBanner = () => {
  const { fetchClient } = useApiClient();

  const fetchBanners = async (adminMode: boolean = false) => {
    const url = adminMode ? "/banners/admin" : "/banners";
    return await fetchClient<Banner[]>(url);
  };

  const createBanner = async (data: BannerCreateRequest, file: File) => {
    const formData = new FormData();
    formData.append("title", data.title);
    if (data.linkUrl) formData.append("linkUrl", data.linkUrl);
    formData.append("isVisible", data.isVisible.toString());
    formData.append("orderIndex", data.orderIndex.toString());
    formData.append("image", file);

    return await fetchClient<Banner>("/banners/admin", {
      method: "POST",
      body: formData,
    });
  };

  const deleteBanner = async (id: number) => {
    return await fetchClient<void>(`/banners/admin/${id}`, {
      method: "DELETE",
    });
  };

  return {
    fetchBanners,
    createBanner,
    deleteBanner,
  };
};
