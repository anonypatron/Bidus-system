import axiosInstance from '../app/utils/axiosInstance';

export const salesRequest = async (formData: FormData) => {
    const res = await axiosInstance.post('/auctions', formData);

    if (res.status === 413) {
        throw new Error('파일의 크기가 너무 큽니다.');
    }

    return { success: true };
};