'use client';

import { useState } from 'react';
import { LuSearch } from 'react-icons/lu';
import { SearchModal } from '../modal/SearchModal';

function SearchBox() {
    const [searchTerm, setSearchTerm] = useState('');
    const [isModalOpen, setIsModalOpen] = useState(false);

    const handleOpenModal = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setIsModalOpen(true);
    };

    return (
        <>
            <form className="search-box-form" onSubmit={handleOpenModal}>
                <input
                    type="text"
                    className="search-input"
                    placeholder="상세 검색..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
                <button type="submit" className="search-button">
                    <LuSearch />
                </button>
            </form>

            <SearchModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                initialKeyword={searchTerm.trim()}
            />
        </>
    );
}

export default SearchBox;