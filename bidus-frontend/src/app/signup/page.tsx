'use client';

import { useState } from 'react';
import { SignupFormData } from '../../../types/dto/request/login';
import { useSignup } from '../../hooks/useSignup';

function SignUpPage() {
    const { mutate: signup, isPending, isError, error } = useSignup();
    const [formData, setFormData] = useState<SignupFormData>({
        email: "",
        username: "",
        password: "",
        role: "USER",
    });
    const [passwordConfirm, setPasswordConfirm] = useState<string>("");
    const [clientError, setClientError] = useState<string>('');

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setClientError('');

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

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (formData.password !== passwordConfirm) {
            setClientError('비밀번호가 일치하지 않습니다.');
            return;
        }

        signup(formData);
    };

    return (
        <div className="signup-container">
            <div className="signup-card">
                <h2 className="signup-title">회원가입</h2>
                <form onSubmit={handleSubmit} className="signup-form">
                    <div className="form-group">
                        <label htmlFor="username">사용자 이름</label>
                        <input
                            type="text"
                            id="username"
                            className="form-input"
                            name="username"
                            placeholder="사용자 이름"
                            required
                            onChange={handleChange}
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="email">이메일</label>
                        <input
                            type="email"
                            id="email"
                            className="form-input"
                            name="email"
                            placeholder="이메일"
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

                    {clientError && <div className="error-message">{clientError}</div>}
                    {isError && <div className="error-message">{(error).message}</div>}

                    <div className="form-actions">
                        <button type="submit" className="signup-button">
                            {isPending ? '가입 처리 중...' : '회원가입'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default SignUpPage;