'use client';

import { signupRequest } from '../api/signupRequest';
import { useMutation } from "@tanstack/react-query";
import { useRouter } from "next/navigation";

export const useSignup = () => {
    const router = useRouter();

    return useMutation({
        mutationFn: signupRequest,
        onSuccess: () => {
            router.push('/login');
        },
        onError: (error: any) => {
            alert(`회원가입 실패: ${error.message}`)
        },
    });
};