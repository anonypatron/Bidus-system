import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';
import { jwtVerify } from 'jose';

export async function middleware(request: NextRequest) {
    const accessToken = request.cookies.get('accessToken')?.value;
    const secret = new TextEncoder().encode(process.env.JWT_SECRET);

    const originalPath = request.nextUrl.pathname;
    const loginUrl = new URL('/login', request.url);
    loginUrl.searchParams.set('redirectUrl', originalPath);

    if (!accessToken) {
        return NextResponse.redirect(loginUrl);
    }

    try {
        await jwtVerify(accessToken, secret);
        
        return NextResponse.next();
    } catch (error) {
        console.error("JWT 검증 실패:", error);
        // loginUrl.searchParams.set('message', '세션이 만료되었습니다. 다시 로그인해주세요');
        return NextResponse.redirect(loginUrl);
    }
}

export const config = {
    matcher: [
        '/dashboard/:path*',
        '/library/:path*',
        '/bookmark/:path*',
        '/sales/:path*',
        '/setting/:path*',
        '/customer-service-center/:path*',
        '/auctions/:path*',
    ],
};