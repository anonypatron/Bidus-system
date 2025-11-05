import { LoginFormData } from "../../types/dto/request/login"

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

export const loginRequest = async (formData: LoginFormData) => {
    const res = await fetch(`${API_BASE_URL}` + '/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
    });

    if (!res.ok) {
        if (res.status === 401) {
            throw new Error('이메일 또는 비밀번호가 일치하지 않습니다.');
        }
        
        const errorData = await res.json().catch(() => null);
        throw new Error(errorData?.message || '로그인 중 문제가 발생했습니다.');
    }
};