export interface AuctionCreateFormData {
    title: string;
    description: string;
    categories: Array<string>;
    startPrice: number;
    startTime: string; // ex) 2025-06-07 07:00:00
    endTime: string;
}

export interface AuctionUpdateFormData {
    title: string;
    description: string;
    categories: Array<string>;
    startPrice: number;
    startTime: string; // ex) 2025-06-07 07:00:00
    endTime: string;
}

export interface FormErrors {
    endTime?: string;
}

export interface CategoryOption {
    readonly label: string;
    readonly value: string;
}

export interface CategorySelectorProps {
    value: readonly CategoryOption[];
    onChange: (newValue: readonly CategoryOption[]) => void;
    options: readonly CategoryOption[];
}

export interface BidForm {
    auctionId: number;
    price: number;
}