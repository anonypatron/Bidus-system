export enum Role {
    ADMIN, USER
}

export interface SignupFormData {
    email: string;
    username: string;
    password: string;
    role: string;
}

export interface LoginFormData {
    email: string;
    password: string;
}

export interface UpdateFormData {
    username: string;
    password: string;
}