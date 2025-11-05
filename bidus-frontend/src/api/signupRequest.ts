import { SignupFormData } from "../../types/dto/request/login";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

export const signupRequest = async (formData: SignupFormData) => {
    const res = await fetch(`${API_BASE_URL}` + '/api/auth/signup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
    });

    if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || '회원가입에 실패했습니다.');
    }
};