export type ProductStatus =
  | "SALE"
  | "RESERVED"
  | "SOLD"
  | "PENDING"
  | "HIDDEN"
  | "DELETED";

export interface Product {
  id: number;
  title: string;
  description: string | null;
  price: number;
  status: ProductStatus;
  category: string;
  seller: {
    id: number;
    username: string;
    displayName: string;
    role: string;
  };
  viewCount: number;
  createdAt: string;
  updatedAt: string;
  realEstate?: ProductRealEstate;
  images: ProductImage[];
}

export interface ProductRealEstate {
  locationCity: string;
  locationDistrict: string;
  pcCount: number;
  deposit: number;
  monthlyRent: number;
  managementFee: number;
  averageMonthlyRevenue: number;
  rightsMoney: number;
  floor: number | null;
  areaMeters: number | null;
  areaPyeong: number | null;
  facilities: string | null;
  moveInDate: string | null;
  permitStatus: string | null;
  adminActionHistory: string | null;
  contactNumber: string | null;
}

export interface ProductImage {
  id: number;
  url: string;
  type: "IMAGE" | "VIDEO";
  orderIndex: number;
}

export interface ProductCreateRequest {
  title: string;
  description: string;
  price: number;
  category: string;
  realEstate?: ProductRealEstate;
}

export interface ProductUpdateRequest {
  title?: string;
  description?: string;
  price?: number;
  status?: ProductStatus;
  category?: string;
  realEstate?: ProductRealEstate;
}

export const useMarket = () => {
  const { fetchClient } = useApiClient();

  const fetchProducts = async (
    page: number = 1,
    size: number = 20,
    category?: string,
  ) => {
    return await fetchClient<{ status: string; data: Product[] }>(
      "/v1/market/products",
      {
        params: { page, size, category },
      },
    );
  };

  const fetchProductById = async (id: string | number) => {
    return await fetchClient<{ status: string; data: Product }>(
      `/v1/market/products/${id}`,
    );
  };

  const createProduct = async (data: ProductCreateRequest, files: File[]) => {
    const formData = new FormData();
    formData.append("product", JSON.stringify(data));
    files.forEach((file) => {
      formData.append("file", file);
    });

    return await fetchClient<{ status: string; data: Product }>(
      "/v1/market/products",
      {
        method: "POST",
        body: formData,
      },
    );
  };

  const updateProduct = async (id: number, data: ProductUpdateRequest) => {
    return await fetchClient<{ status: string; data: Product }>(
      `/v1/market/products/${id}`,
      {
        method: "PUT",
        body: data,
      },
    );
  };

  const deleteProduct = async (id: number) => {
    return await fetchClient<{ status: string }>(`/v1/market/products/${id}`, {
      method: "DELETE",
    });
  };

  const updateProductStatus = async (id: number, status: ProductStatus) => {
    return await fetchClient<{ status: string; newStatus: ProductStatus }>(
      `/v1/market/products/${id}/status`,
      {
        method: "PUT",
        body: { status },
      },
    );
  };

  return {
    fetchProducts,
    fetchProductById,
    createProduct,
    updateProduct,
    deleteProduct,
    updateProductStatus,
  };
};
