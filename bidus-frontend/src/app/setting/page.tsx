'use client';

import { useRouter } from "next/navigation";
import { useContext, useEffect, useState } from "react";
import { UpdateFormData } from '../../../types/dto/request/login';
import { UserContext } from '../context/UserProvider';
import axiosInstance from '../utils/axiosInstance';
import LoadingSpinner from "../components/LoadingSpinner";

function SettingPage() {
    const router = useRouter();
    const userContext = useContext(UserContext);

    const [passwordConfirm, setPasswordConfirm] = useState<string>("");
    const [error, setError] = useState<string>('');
    const [formData, setFormData] = useState<UpdateFormData>({
        username: "",
        password: "",
    });

    useEffect(() => {
        if (userContext?.userInfo) {
            setFormData(prev => ({ ...prev, username: userContext.userInfo.username }));
        } else if (!userContext) {
            alert('로그인이 필요한 서비스입니다.');
            router.push('/login');
        }
    }, [userContext, router]);

    if (!userContext || !userContext.userInfo) {
        return <LoadingSpinner/>
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;

        if (name === "passwordConfirm") {
            setPasswordConfirm(value);
        }
        else {
            setFormData(prevState => ({
                ...prevState,
                [name]: value,
            }));
        }
        
    };

    const updateUserInfo = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (formData.password && formData.password !== passwordConfirm) {
            setError('새 비밀번호가 일치하지 않습니다.');
            return;
        }

        try {
            const res = await axiosInstance.patch('http://localhost/api/auth', formData);
            userContext.refreshUserInfo();
            router.push('/login');
        } catch(error) {
            console.log('정보 수정 시 에러: ' + error);
            alert('정보 수정 실패: ' + error);
        }
    };

    const withdraw = async () => {
        if (confirm("정말로 탈퇴하시겠습니까?")) {
            try {
                const res = await axiosInstance.delete('/auth');
                alert('탈퇴가 완료되었습니다.')
                router.replace('/');
            } catch (err: any) {
                console.error(err);
            }
        }
    };
    
    return (
        <div className="signup-container">
            <div className="signup-card">
                <h2 className="signup-title">정보 수정</h2>
                <form onSubmit={updateUserInfo} className="signup-form">
                    <div className="form-group">
                        <label htmlFor="username">사용자 이름</label>
                        <input
                            type="text"
                            id="username"
                            className="form-input"
                            name="username"
                            placeholder="사용자 이름"
                            value={formData.username}
                            required
                            onChange={handleChange}
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">비밀번호</label>
                        <input
                            type="password"
                            id="password"
                            className="form-input"
                            name="password"
                            placeholder="비밀번호"
                            value={formData.password}
                            required
                            onChange={handleChange}
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="passwordConfirm">비밀번호 확인</label>
                        <input
                            type="password"
                            id="passwordConfirm"
                            className="form-input"
                            name="passwordConfirm"
                            placeholder="비밀번호 확인"
                            required
                            onChange={handleChange}
                        />
                    </div>
                    {error &&
                        <div className="error-message">
                            {error}
                        </div>
                    }
                    <div className="form-actions">
                        <button type="submit" className="signup-button">수정 완료</button>
                    </div>
                </form>
                <div className="withdraw-container">
                    <button onClick={withdraw} className="withdraw-button">
                        회원 탈퇴
                    </button>
                </div>
            </div>
        </div>
    );
}

export default SettingPage;