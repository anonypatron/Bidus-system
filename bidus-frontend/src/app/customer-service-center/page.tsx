'use client';

import { useState } from "react";
import { LuChevronDown, LuChevronUp, LuSearch, LuCircle, LuMessageCircle, LuPhone } from "react-icons/lu";

interface FaqItem {
    id: number;
    category: string;
    question: string;
    answer: string;
}

// 샘플 데이터
const FAQ_DATA: FaqItem[] = [
    {
        id: 1,
        category: '경매 이용',
        question: '경매 입찰은 어떻게 진행하나요?',
        answer: '원하시는 물품 상세 페이지에서 "입찰하기" 버튼을 눌러 희망 가격을 입력하시면 됩니다. 현재 최고가보다 높은 금액으로만 입찰이 가능합니다.'
    },
    {
        id: 2,
        category: '계정',
        question: '회원 탈퇴는 어떻게 하나요?',
        answer: '마이페이지 > 개인설정 메뉴 하단의 "회원 탈퇴" 버튼을 통해 진행하실 수 있습니다. 진행 중인 경매가 있을 경우 탈퇴가 불가능합니다.'
    },
    {
        id: 3,
        category: '판매',
        question: '판매 물품 등록 시 수수료가 있나요?',
        answer: '물품 등록은 무료입니다. 단, 낙찰 확정 시 최종 낙찰가의 5%가 수수료로 부과됩니다.'
    },
];

function CustomerServiceCenter() {
    const [openFaqId, setOpenFaqId] = useState<number | null>(null);

    const toggleFaq = (id: number) => {
        setOpenFaqId(openFaqId === id ? null : id);
    };

    return (
        <div className="cs-container">
            <section className="cs-quick-menu">
                <div className="quick-card">
                    <div className="icon-wrapper"><LuCircle /></div>
                    <h3>자주 묻는 질문</h3>
                    <p>가장 많이 찾는 질문들</p>
                </div>
                <div className="quick-card">
                    <div className="icon-wrapper"><LuMessageCircle /></div>
                    <h3>1:1 문의</h3>
                    <p>전문 상담원 연결</p>
                </div>
                <div className="quick-card">
                    <div className="icon-wrapper"><LuPhone /></div>
                    <h3>전화 상담</h3>
                    <p>평일 09:00 - 18:00</p>
                </div>
            </section>

            {/* 3. FAQ 아코디언 */}
            <section className="cs-faq-section">
                <h2 className="section-heading">자주 묻는 질문 (FAQ)</h2>
                <div className="faq-list">
                    {FAQ_DATA.map((item) => (
                        <div key={item.id} className={`faq-item ${openFaqId === item.id ? 'open' : ''}`}>
                            <button className="faq-question" onClick={() => toggleFaq(item.id)}>
                                <span className="faq-category">{item.category}</span>
                                <span className="faq-text">{item.question}</span>
                                {openFaqId === item.id ? <LuChevronUp /> : <LuChevronDown />}
                            </button>
                            <div className="faq-answer">
                                <div className="answer-content">
                                    {item.answer}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </section>

            {/* 4. 하단 안내 배너 */}
            <section className="cs-contact-banner">
                <p>원하시는 답변을 찾지 못하셨나요?</p>
                <button className="contact-button">1:1 문의하기</button>
                <p className="contact-info">
                    고객센터 010-1234-5678 &nbsp;|&nbsp; 평일 09:00 - 18:00 (주말/공휴일 휴무)
                </p>
            </section>
        </div>
    );
}

export default CustomerServiceCenter;