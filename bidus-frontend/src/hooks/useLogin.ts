'use client';

import { useMutation } from "@tanstack/react-query";
import { useRouter, useSearchParams } from "next/navigation";
import { loginRequest } from '../api/loginRequest';
import { useContext } from "react";
import { UserContext } from '../app/context/UserProvider';

export const useLogin = () => {
    const userContext = useContext(UserContext);
    const searchParams = useSearchParams();
    const router = useRouter();

    return useMutation({
        mutationFn: loginRequest,
        onSuccess: () => {
            if (!userContext) {
                console.log('userContext missing');
                return;
            }
            
            const redirectUrl = searchParams.get('redirectUrl');
            userContext.refreshUserInfo();
            
            if (redirectUrl) {
                router.push(redirectUrl);
            }
            else {
                router.push('/');
            }
        },
        onError: (error: any) => {
            console.log(error);
        },
    });
};