import type { Metadata } from 'next';
import "./globals.css";
import Providers from './providers';
import LayoutWrapper from './components/layout/LayoutWrapper';

export const metadata: Metadata = {
  title: "Bidus",
  description: "다양한 물건을 실시간으로 거래합니다.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko" suppressHydrationWarning>
      <body>
        <Providers>
          <LayoutWrapper>
            {children}
          </LayoutWrapper>
        </Providers>
      </body>
    </html>
  );
}
