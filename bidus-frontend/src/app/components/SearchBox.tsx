'use client';

import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { LuSearch } from 'react-icons/lu';

function SearchBox() {
    const [searchTerm, setSearchTerm] = useState('');
    const router = useRouter();

    const handleSearch = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const trimmedSearchString = searchTerm.trim();
        if (!trimmedSearchString) {
            return;
        }

        router.push(`/search?q=${trimmedSearchString}`);
    };

    return (
        <form className="search-box-form" onSubmit={handleSearch}>
        <input
            type="text"
            className="search-input"
            placeholder="상품을 검색..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
        />
        <button type="submit" className="search-button">
            <LuSearch />
        </button>
        </form>
    );
}

export default SearchBox;