'use client';

import { useMutation } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';
import { salesRequest } from '../api/salesRequest';

export const useSales = () => {
    const router = useRouter();
    
    return useMutation({
        mutationFn: salesRequest,
        onSuccess: () => {
            router.push('/');
        },
        onError: (error: any) => {
            console.log(error);
        },
    });
};