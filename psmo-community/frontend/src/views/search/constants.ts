export interface SortOption {
  value: string
  label: string
}

export const categories = ['전체', '질문', '정보공유', '자유게시판', '프로젝트', '스터디', '채용']

export const sortOptions: SortOption[] = [
  { value: 'latest', label: '최신순' },
  { value: 'popular', label: '인기순' },
  { value: 'views', label: '조회순' },
  { value: 'comments', label: '댓글순' },
]
