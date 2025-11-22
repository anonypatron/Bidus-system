'use client';

import axios from 'axios';
import dynamic from 'next/dynamic';
import { useEffect, useState } from 'react';
import DatePicker from 'react-datepicker';
import { AuctionUpdateFormData, CategoryOption, FormErrors } from '../../../../types/dto/request/auction';
import { AuctionUpdateModalProps } from '../../../../types/others/modal';
import axiosInstance from '../../utils/axiosInstance';

const CategorySelector = dynamic(() => import('../category/CategorySelector'), { 
    ssr: false,
    loading: () => <p>로딩 중...</p>
});

const predefinedCategories: readonly CategoryOption[] = [
    { value: 'collectibles', label: '수집품' },
    { value: 'electronics', label: '전자기기' },
    { value: 'books', label: '도서' },
    { value: 'fashion', label: '패션/의류' },
    { value: 'furniture', label: '가구/인테리어' },
];

function AuctionUpdateModal({ auctionId, onClose, onUpdateSuccess }: AuctionUpdateModalProps) {
    const [errors, setErrors] = useState<FormErrors>({});
    const [errorMessage, setErrorMessage] = useState('');
    const [imageFile, setImageFile] = useState<File | null>(null);
    const [imagePreview, setImagePreview] = useState<string | null>(null);
    const [selectedStartTime, setSelectedStartTime] = useState<Date | null>(null);
    const [selectedEndTime, setSelectedEndTime] = useState<Date | null>(null);
    const [selectedCategories, setSelectedCategories] = useState<readonly CategoryOption[]>([]);
    const [auctionUpdateForm, setAuctionUpdateForm] = useState<AuctionUpdateFormData>({
        title: '',
        description: '',
        categories: [],
        startPrice: 0,
        startTime: '',
        endTime: '',
    });

    useEffect(() => {
        if (!auctionId) {
            return;
        }

        const fetchAuctionDetails = async () => {
            try {
                const res = await axiosInstance.get(`/auctions/${auctionId}`);
                const data = res.data
                console.log(data);

                setAuctionUpdateForm({
                    title: data.title,
                    description: data.description,
                    categories: data.categories, // 서버에서 받은 카테고리 문자열 배열
                    startPrice: data.startPrice,
                    startTime: data.startTime,   // 서버에서 받은 시간 문자열
                    endTime: data.endTime,       // 서버에서 받은 시간 문자열
                });

                setSelectedStartTime(new Date(data.startTime));
                setSelectedEndTime(new Date(data.endTime));
                const categoryOptions = data.categories.map((cat: string) => ({ 
                    value: cat, 
                    label: cat,
                }));
                setSelectedCategories(categoryOptions);
                setImagePreview(data.imagePath); // 기존 이미지 URL로 미리보기 설정

            } catch (error) {
                console.error("경매 정보를 불러오는 데 실패했습니다:", error);
            }
        };

        fetchAuctionDetails();

    }, [auctionId]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        
        if (name === 'startPrice') {
            setAuctionUpdateForm(prev => ({...prev, [name]: Number(value)}));
        } else {
            setAuctionUpdateForm(prev => ({...prev, [name]: value}));
        }
    };

    const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            const file = e.target.files[0];
            setImageFile(file); // 파일 상태 저장

            // 이미지 미리보기 URL 생성
            const previewUrl = URL.createObjectURL(file);
            setImagePreview(previewUrl);
        }
    };

    const handleDateChange = (date: Date | null, name: 'startTime' | 'endTime') => {
        if (!date) return;

        const utcDateTimeString = date.toISOString();

        if (name === 'startTime') {
            setSelectedStartTime(date);
            setAuctionUpdateForm(prev => ({ ...prev, startTime: utcDateTimeString }));
            if (selectedEndTime && selectedEndTime <= date) {
                setErrors(prev => ({ ...prev, endTime: '종료 시간은 시작 시간보다 빨라야 합니다.' }));
            } else {
                setErrors(prev => ({ ...prev, endTime: undefined }));
            }
        } else { // name === 'endTime'
            setSelectedEndTime(date);
            setAuctionUpdateForm(prev => ({ ...prev, endTime: utcDateTimeString }));
            if (selectedStartTime && date <= selectedStartTime) {
                setErrors(prev => ({ ...prev, endTime: '종료 시간은 시작 시간보다 빨라야 합니다.' }));
            } else {
                setErrors(prev => ({ ...prev, endTime: undefined }));
            }
        }
    };

    const handleCategoryChange = (newValue: readonly CategoryOption[]) => {
        setSelectedCategories(newValue);
        const categoryValues = newValue.map(option => option.label);
        setAuctionUpdateForm(prev => ({ ...prev, categories: categoryValues }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (errors.endTime) {
            alert(imageFile ? '입력 값을 확인해주세요.' : '상품 이미지를 등록해주세요.');
            return;
        }

        const auctionDataBlob = new Blob([JSON.stringify(auctionUpdateForm)], {
            type: 'application/json',
        });

        const formData = new FormData();

        if (imageFile) {
            formData.append('image', imageFile);
        }
        formData.append('auctionData', auctionDataBlob);
        // console.log(formData);

        try {
            const res = await axiosInstance.patch(`/auctions/${auctionId}`, formData);
            onUpdateSuccess();
            onClose();
        } catch(error: any) {
            if (axios.isAxiosError(error) && error.status === 413) {
                setErrorMessage('파일의 크기가 너무 큽니다.');
            }
            console.log(error);
        }
    };

    const minDate = new Date();

    return (
        <div className="update-page-container">
            <form onSubmit={handleSubmit} className="update-form">
                <h2>경매 업데이트</h2>
                <button onClick={onClose} className="btn-close-modal">
                    X
                </button>
                <div className="form-group">
                    <label className="form-label" htmlFor="image">상품 이미지</label>
                    <div className="image-upload-container">
                        <input
                            id="image"
                            type="file"
                            accept="image/*"
                            onChange={handleImageChange}
                            className="image-upload-input"
                        />
                        <label htmlFor="image" className="image-upload-label">
                            {imagePreview ? (
                                <img src={
                                    imagePreview.startsWith('blob:') 
                                        ? imagePreview 
                                        : `http://localhost${imagePreview}`
                                } 
                                alt="미리보기" 
                                className="image-preview" />
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
                    value={auctionUpdateForm.title}
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
                    value={auctionUpdateForm.description}
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
                    value={auctionUpdateForm.startPrice}
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
                        timeIntervals={15}
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
                        timeIntervals={15}
                        className="form-input"
                        minDate={selectedStartTime || minDate}
                        placeholderText="경매 종료 시간을 선택하세요"
                        required
                    />
                    {errors.endTime && <p className="error-message">{errors.endTime}</p>}
                </div>
                {errorMessage && 
                    <div className='error-message'>
                        {errorMessage}
                    </div>
                }
                
                <button type="submit" className="submit-button" disabled={!!errors.endTime}>
                    아이템 등록
                </button>
            </form>
        </div>
    );
}

export default AuctionUpdateModal;