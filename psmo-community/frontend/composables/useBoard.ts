export enum BoardCategory {
  NOTICE = "NOTICE",
  FREE = "FREE",
  QA = "QA",
}

export enum BoardSubCategory {
  // Notice
  MUST_READ = "MUST_READ",
  UPDATE = "UPDATE",
  EVENT = "EVENT",
  // Free
  CHAT = "CHAT",
  HUMOR = "HUMOR",
  INFO = "INFO",
  // QA
  HARDWARE = "HARDWARE",
  SOFTWARE = "SOFTWARE",
  OPERATION = "OPERATION",
  ETC = "ETC",
}

export interface Post {
  id: number;
  title: string;
  content: string;
  category: BoardCategory;
  subCategory?: BoardSubCategory;
  author: {
    id: number;
    username: string;
    displayName: string;
    role: string;
  };
  viewCount: number;
  likeCount: number;
  commentCount: number;
  createdAt: string;
  imageUrls: string[];
  isLiked: boolean;
}

export interface Comment {
  id: number;
  content: string;
  author: {
    id: number;
    username: string;
    displayName: string;
  };
  createdAt: string;
}

export interface PostCreateRequest {
  title: string;
  content: string;
  category: BoardCategory;
  subCategory?: BoardSubCategory;
  imageUrls: string[];
}

export const useBoard = () => {
  const { fetchClient } = useApiClient();

  const fetchPosts = async (
    page: number = 1,
    size: number = 20,
    category?: string,
    subCategory?: string,
  ) => {
    return await fetchClient<{ status: string; data: Post[] }>("/board/posts", {
      params: { page, size, category, subCategory },
    });
  };

  const fetchPostById = async (id: string | number) => {
    return await fetchClient<{ status: string; data: Post }>(
      `/board/posts/${id}`,
    );
  };

  const createPost = async (data: PostCreateRequest) => {
    return await fetchClient<{ status: string; data: Post }>("/board/posts", {
      method: "POST",
      body: data,
    });
  };

  const fetchComments = async (postId: number) => {
    return await fetchClient<{ status: string; data: Comment[] }>(
      `/board/posts/${postId}/comments`,
    );
  };

  const createComment = async (postId: number, content: string) => {
    return await fetchClient<{ status: string; data: Comment }>(
      `/board/posts/${postId}/comments`,
      {
        method: "POST",
        body: { content },
      },
    );
  };

  const toggleLike = async (postId: number) => {
    return await fetchClient<{ status: string; isLiked: boolean }>(
      `/board/posts/${postId}/like`,
      {
        method: "POST",
      },
    );
  };

  return {
    fetchPosts,
    fetchPostById,
    createPost,
    fetchComments,
    createComment,
    toggleLike,
  };
};
