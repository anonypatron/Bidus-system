'use client';

import CreatableSelect from 'react-select/creatable';
import { CategorySelectorProps } from '../../../types/dto/request/auction';

function CategorySelector({ value, onChange, options }: CategorySelectorProps) {
    return (
        <CreatableSelect
            id="categories"
            instanceId="category-selector" // 고유한 instanceId를 부여하여 ID 충돌 방지
            isMulti
            options={options}
            value={value}
            onChange={onChange}
            placeholder="카테고리를 선택하거나 직접 입력하세요..."
            styles={{
                control: (baseStyles, state) => ({
                    ...baseStyles,
                    borderColor: state.isFocused ? 'var(--primary-color)' : 'var(--border-color)',
                    boxShadow: state.isFocused ? '0 0 0 3px rgba(0, 123, 255, 0.25)' : 'none',
                    borderRadius: '8px',
                    padding: '4px',
                }),
                multiValue: (baseStyles) => ({ ...baseStyles, backgroundColor: '#e7f3ff', borderRadius: '4px' }),
                multiValueLabel: (baseStyles) => ({ ...baseStyles, color: 'var(--primary-color)', fontWeight: '500' }),
            }}
        />
    );
}

export default CategorySelector;