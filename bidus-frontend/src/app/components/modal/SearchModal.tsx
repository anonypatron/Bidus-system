'use client';

import dynamic from 'next/dynamic';
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { CategoryOption } from '../../../../types/dto/request/auction';

const predefinedCategories: readonly CategoryOption[] = [
    { value: 'collectibles', label: '수집품' },
    { value: 'electronics', label: '전자기기' },
    { value: 'books', label: '도서' },
    { value: 'fashion', label: '패션/의류' },
    { value: 'furniture', label: '가구/인테리어' },
];

const CategorySelector = dynamic(() => import('../../components/category/CategorySelector'), { 
    ssr: false,
    loading: () => <p>로딩 중...</p> // 로딩 중에 보여줄 UI
});

// interface SearchParams {
//     keyword?: string;
//     status?: string;
//     category?: string;
//     sort?: string;
// }

interface Props {
    isOpen: boolean;
    onClose: () => void;
    initialKeyword: string;
}

const sortOptions = [
    { label: '마감 임박순', value: 'endTime,asc' },
    { label: '최근 등록순', value: 'startTime,desc' },
    { label: '가격 낮은순', value: 'currentPrice,asc' },
    { label: '가격 높은순', value: 'currentPrice,desc' },
];

export const SearchModal = ({ isOpen, onClose, initialKeyword }: Props) => {
    const router = useRouter();

    const [selectedCategories, setSelectedCategories] = useState<readonly CategoryOption[]>([]);
    const [keyword, setKeyword] = useState(initialKeyword);
    const [status, setStatus] = useState('');
    const [sort, setSort] = useState(sortOptions[0].value);

    useEffect(() => {
        setKeyword(initialKeyword);
    }, [initialKeyword, isOpen]);

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const params = new URLSearchParams();

        if (keyword.trim()) {
            params.set('keyword', keyword.trim());
        }
        if (status) {
            params.set('status', status);
        }
        if (sort) {
            params.set('sort', sort);
        }

        const categoryValues = selectedCategories.map(option => option.label);

        if (categoryValues.length > 0) {
            params.set('categories', categoryValues.join(','));
        }

        router.push(`/search?${params.toString()}`);
        setSelectedCategories([]);
        onClose();
    };

    const handleCategoryChange = (newValue: readonly CategoryOption[]) => {
        setSelectedCategories(newValue);
    };

    if (!isOpen) {
        return;
    }

    return (
        <div className='overlay' onClick={onClose}>
            <div className='modalContent' onClick={(e) => e.stopPropagation()}>
                <button className='closeButton' onClick={onClose}>
                &times;
                </button>
                
                <h3 className="searchModalH3">상세 검색</h3>
                <form onSubmit={handleSubmit}>
                    <div className='formGroup'>
                        <label htmlFor="keyword">키워드</label>
                        <input
                            id="keyword"
                            type="text"
                            value={keyword}
                            onChange={(e) => setKeyword(e.target.value)}
                            placeholder="검색어 입력..."
                        />
                    </div>
                    <div className='formGroup'>
                        <label htmlFor="status">경매 상태</label>
                        <select
                            id="status"
                            value={status}
                            onChange={(e) => setStatus(e.target.value)}
                        >
                        <option value="">전체</option>
                        <option value="IN_PROGRESS">진행중</option>
                        <option value="SCHEDULED">예정</option>
                        <option value="CLOSED">종료</option>
                        </select>
                    </div>
                    <div className='formGroup'>
                        <label className="form-label" htmlFor="categories">카테고리</label>
                        <CategorySelector
                            options={predefinedCategories}
                            value={selectedCategories}
                            onChange={handleCategoryChange}
                        />
                    </div>
                    <div className='formGroup'>
                        <label htmlFor="sort">정렬</label>
                        <select
                        id="sort"
                        value={sort}
                        onChange={(e) => setSort(e.target.value)}
                        >
                        {sortOptions.map((opt) => (
                            <option key={opt.value} value={opt.value}>
                            {opt.label}
                            </option>
                        ))}
                        </select>
                    </div>
                    <button type="submit" className='submitButton'>
                        검색하기
                    </button>
                </form>
            </div>
        </div>
    );
}