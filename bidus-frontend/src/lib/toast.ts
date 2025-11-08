'use client';

import { toast as sonnerToast } from 'sonner';

type ToastOptions = {
  description?: React.ReactNode;
  duration?: number;
  [key: string]: any;
};

const success = (message: React.ReactNode, options?: ToastOptions) => {
  sonnerToast.success(message, options);
};

const error = (message: React.ReactNode | Error, options?: ToastOptions) => {
  const errorMessage = message instanceof Error ? message.message : message;

  console.error(`[Toast Error]:`, errorMessage, options);

  sonnerToast.error(errorMessage, options);
};

const info = (message: React.ReactNode, options?: ToastOptions) => {
  sonnerToast.info(message, options);
};

const warning = (message: React.ReactNode, options?: ToastOptions) => {
  sonnerToast.warning(message, options);
};

const custom = (message: React.ReactNode, options?: ToastOptions) => {
  sonnerToast(message, options);
};

export const toast = {
    ...sonnerToast,
    success,
    error,
    info,
    warning,
    custom,
};