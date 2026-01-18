export interface Product {
  id: number;
  title: string;
  description: string | null;
  price: number;
  status: "SALE" | "RESERVED" | "SOLD";
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
  floor: number | null;
  areaMeters: number | null;
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

    // Backend expects 'product' part as JSON string
    formData.append("product", JSON.stringify(data));

    // Backend expects 'images' part or individual file parts?
    // Controller: multipart.forEachPart...
    // if PartData.FileItem -> uploads it. Name doesn't seem to be strictly checked for "images", just any file item.
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

  return {
    fetchProducts,
    fetchProductById,
    createProduct,
  };
};
