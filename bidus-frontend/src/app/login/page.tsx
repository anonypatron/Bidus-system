'use client';

import { Suspense, useState } from "react";
import { LoginFormData } from '../../../types/dto/request/login';
import { useLogin } from '../../hooks/useLogin';

function LoginContent() {
    const { mutate: login, isPending, isError, error } = useLogin();
    const [clientError, setClientError] = useState<string>('');
    const [formData, setFormData] = useState<LoginFormData>({
        email: "",
        password: "",
    });
    
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setClientError('');

        setFormData(prevState => ({
            ...prevState,
            [name]: value,
        }));
    };

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        login(formData);
    };

    return (
        <div className="login-container">
            <div className="login-card">
                <h2 className="login-title">로그인</h2>
                <form onSubmit={ handleSubmit } className="login-form">
                    <div className="form-group">
                        <label htmlFor="email">이메일</label>
                        <input
                            type="email"
                            name="email"
                            className="form-input"
                            id="email"
                            required
                            placeholder="이메일을 입력하세요"
                            onChange={ handleChange }
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">비밀번호</label>
                        <input
                            type="password"
                            name="password"
                            className="form-input"
                            id="password"
                            required
                            placeholder="비밀번호를 입력하세요"
                            onChange={ handleChange }
                        />
                    </div>
                    
                    {clientError && <div className="error-message">{clientError}</div>}
                    {isError && <div className="error-message">{(error).message}</div>}

                    <button type="submit" className="signup-button">
                        {isPending ? '로그인 중...' : '로그인'}
                    </button>
                </form>
            </div>
        </div>
    );
}

function LoginPage() {
    return (
        <Suspense fallback={<div className="login-container">로딩 중...</div>}>
            <LoginContent />
        </Suspense>
    );
}

export default LoginPage;