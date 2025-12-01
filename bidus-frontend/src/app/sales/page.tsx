'use client';

import dynamic from 'next/dynamic';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import DatePicker from 'react-datepicker';
import { AuctionCreateFormData, CategoryOption, FormErrors } from '../../../types/dto/request/auction';
import { useSales } from '../../hooks/useSales';

const CategorySelector = dynamic(() => import('../components/category/CategorySelector'), { 
    ssr: false,
    loading: () => <p>로딩 중...</p> // 로딩 중에 보여줄 UI
});

const predefinedCategories: readonly CategoryOption[] = [
    { value: 'collectibles', label: '수집품' },
    { value: 'electronics', label: '전자기기' },
    { value: 'books', label: '도서' },
    { value: 'fashion', label: '패션/의류' },
    { value: 'furniture', label: '가구/인테리어' },
];

function SalesPage() {
    const { mutate: sales, isPending, isError, error } = useSales();
    const [auctionCreateForm, setAuctionCreateForm] = useState<AuctionCreateFormData>({
        title: '',
        description: '',
        categories: [],
        startPrice: 0,
        startTime: '',
        endTime: '',
    });

    const [imageFile, setImageFile] = useState<File | null>(null);
    const [imagePreview, setImagePreview] = useState<string | null>(null);

    const [clientError, setClientError] = useState<string>('');
    const [errors, setErrors] = useState<FormErrors>({});
    const [selectedStartTime, setSelectedStartTime] = useState<Date | null>(null);
    const [selectedEndTime, setSelectedEndTime] = useState<Date | null>(null);
    const [selectedCategories, setSelectedCategories] = useState<readonly CategoryOption[]>([]);

    const router = useRouter();

    const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            const file = e.target.files[0];
            setImageFile(file); // 파일 상태 저장

            // 이미지 미리보기 URL 생성
            const previewUrl = URL.createObjectURL(file);
            setImagePreview(previewUrl);
        }
    };

    // 👇 3. 컴포넌트 언마운트 시 미리보기 URL 메모리 해제 (메모리 누수 방지)
    useEffect(() => {
        return () => {
            if (imagePreview) {
                URL.revokeObjectURL(imagePreview);
            }
        };
    }, [imagePreview]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setClientError('');

        if (name === 'startPrice') {
            setAuctionCreateForm(prev => ({...prev, [name]: Number(value)}));
        } else {
            setAuctionCreateForm(prev => ({...prev, [name]: value}));
        }
    };
    
    const handleCategoryChange = (newValue: readonly CategoryOption[]) => {
        setSelectedCategories(newValue);
        // DTO에 맞는 string[] 형태로 변환하여 저장
        const categoryValues = newValue.map(option => option.label);
        setAuctionCreateForm(prev => ({ ...prev, categories: categoryValues }));
    };

    const handleDateChange = (date: Date | null, name: 'startTime' | 'endTime') => {
        if (!date) return;

        const utcDateTimeString = date.toISOString();

        if (name === 'startTime') {
            setSelectedStartTime(date);
            setAuctionCreateForm(prev => ({ ...prev, startTime: utcDateTimeString }));
            if (selectedEndTime && selectedEndTime <= date) {
                setErrors(prev => ({ ...prev, endTime: '종료 시간은 시작 시간보다 빨라야 합니다.' }));
            } else {
                setErrors(prev => ({ ...prev, endTime: undefined }));
            }
        } else { // name === 'endTime'
            setSelectedEndTime(date);
            setAuctionCreateForm(prev => ({ ...prev, endTime: utcDateTimeString }));
            if (selectedStartTime && date <= selectedStartTime) {
                setErrors(prev => ({ ...prev, endTime: '종료 시간은 시작 시간보다 빨라야 합니다.' }));
            } else {
                setErrors(prev => ({ ...prev, endTime: undefined }));
            }
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (errors.endTime || !imageFile) {
            alert(imageFile ? '입력 값을 확인해주세요.' : '상품 이미지를 등록해주세요.');
            return;
        }

        const auctionDataBlob = new Blob([JSON.stringify(auctionCreateForm)], {
            type: 'application/json',
        });

        const formData = new FormData();

        formData.append('image', imageFile);
        formData.append('auctionData', auctionDataBlob);
        
        sales(formData);
    };

    const minDate = new Date();

    return (
        <div className="sales-page-container">
            <h2>경매 상품 등록</h2>
            <form onSubmit={handleSubmit} className="sales-form">
                <div className="form-group">
                    <label className="form-label" htmlFor="image">* 상품 이미지</label>
                    <div className="image-upload-container">
                        <input
                            id="image"
                            type="file"
                            accept="image/*"
                            onChange={handleImageChange}
                            className="image-upload-input"
                            required
                        />
                        <label htmlFor="image" className="image-upload-label">
                            {imagePreview ? (
                                <img src={imagePreview} alt="미리보기" className="image-preview" />
                            ) : (
                                <>
                                    <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="currentColor" viewBox="0 0 16 16" style={{ color: '#cbd5e1' }}>
                                        <path d="M6.002 5.5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0z"/>
                                        <path d="M2.002 1a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V3a2 2 0 0 0-2-2h-12zm12 1a1 1 0 0 1 1 1v6.5l-3.777-1.947a.5.5 0 0 0-.577.093l-3.71 3.71-2.66-1.772a.5.5 0 0 0-.63.062L1.002 12V3a1 1 0 0 1 1-1h12z"/>
                                    </svg>
                                    <span>클릭하여 이미지 업로드</span>
                                </>
                            )}
                        </label>
                    </div>
                </div>
                <div className="form-group">
                   <label className="form-label" htmlFor="title">* 제목</label>
                   <input
                       id="title"
                       type="text"
                       name="title"
                       className="form-input"
                       value={auctionCreateForm.title}
                       onChange={handleChange}
                       required
                   />
                </div>

                <div className="form-group">
                   <label className="form-label" htmlFor="description">* 설명</label>
                   <textarea
                       id="description"
                       name="description"
                       className="form-input"
                       value={auctionCreateForm.description}
                       onChange={handleChange}
                       required
                       rows={4}
                   />
                </div>
                
                <div className="form-group">
                    <label className="form-label" htmlFor="categories">* 카테고리</label>
                    <CategorySelector
                        options={predefinedCategories}
                        value={selectedCategories}
                        onChange={handleCategoryChange}
                    />
                </div>
                
                <div className="form-group">
                    <label className="form-label" htmlFor="startPrice">* 시작 가격</label>
                    <input
                       id="startPrice"
                       type="number"
                       name="startPrice"
                       className="form-input"
                       value={auctionCreateForm.startPrice}
                       onChange={handleChange}
                       required
                       min="0"
                    />
                </div>
                
                <div className="form-group">
                    <label className="form-label">* 경매 시작 시간</label>
                    <DatePicker
                        selected={selectedStartTime}
                        onChange={(date: Date | null) => handleDateChange(date, 'startTime')}
                        showTimeSelect
                        dateFormat="yyyy년 MM월 dd일 HH:mm"
                        timeFormat="HH:mm"
                        timeIntervals={1}
                        className="form-input"
                        minDate={minDate}
                        placeholderText="경매 시작 시간을 선택하세요"
                        required
                    />
                </div>

                <div className="form-group">
                    <label className="form-label">* 경매 종료 시간</label>
                    <DatePicker
                        selected={selectedEndTime}
                        onChange={(date: Date | null) => handleDateChange(date, 'endTime')}
                        showTimeSelect
                        dateFormat="yyyy년 MM월 dd일 HH:mm"
                        timeFormat="HH:mm"
                        timeIntervals={1}
                        className="form-input"
                        minDate={selectedStartTime || minDate}
                        placeholderText="경매 종료 시간을 선택하세요"
                        required
                    />
                    {errors.endTime && <p className="error-message">{errors.endTime}</p>}
                </div>

                {clientError && <div className="error-message">{clientError}</div>}
                {isError && <div className="error-message">{(error).message}</div>}
                
                <button type="submit" className="submit-button" disabled={!!errors.endTime}>
                    {isPending ? '아이템 등록 중...' : '아이템 등록'}
                </button>
            </form>
        </div>
    );
}

export default SalesPage;