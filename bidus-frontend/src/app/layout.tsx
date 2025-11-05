import type { Metadata } from 'next';
import "./globals.css";
import Providers from './providers';
import LayoutWrapper from './components/LayoutWrapper';

export const metadata: Metadata = {
  title: "Online Auction",
  description: "다양한 물건을 실시간으로 거래합니다.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
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
