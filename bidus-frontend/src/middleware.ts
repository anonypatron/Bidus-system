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