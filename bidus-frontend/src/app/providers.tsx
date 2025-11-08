'use client';

import React from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import UserProvider from './context/UserProvider';
import { ThemeProvider as NextThemesProvider, useTheme } from 'next-themes';
import { Toaster }from 'sonner';

function ThemedToaster() {
    const { theme } = useTheme();

    return (
        <Toaster
            position="top-center"
            theme={theme as 'light' | 'dark' | 'system'}
            richColors
            closeButton
            duration={3000}
        />
    );
}

export default function Providers({ children }: { children: React.ReactNode }) {
  const [queryClient] = React.useState(() => new QueryClient());

  return (
    <NextThemesProvider
      attribute='class'
      defaultTheme='system'
      enableSystem
    >
      <QueryClientProvider client={queryClient}>
          <UserProvider>
              {children}
              <ThemedToaster/>
          </UserProvider>
      </QueryClientProvider>
    </NextThemesProvider>
  );
}