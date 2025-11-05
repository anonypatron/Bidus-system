import Link from "next/link"

function Footer() {
    return (
        <div style={{ padding: '20px', backgroundColor: '#eee', textAlign: 'center', marginTop: 'auto' }}>
            <div>
                <Link href="/">Home</Link> |&nbsp;
                <Link href="/about">About</Link> |&nbsp; {/* 추후 개발 */}
                <Link href="/contact">Contact</Link> {/* 추후 개발 */}
            </div>

            <div style={{marginTop: '10px'}}>
                <p>📞 010-1234-5678 | ✉️ example1@gmail.com</p>
                <p>© 2025 Bidus Inc. All rights reserved.</p>
            </div>

            <div style={{marginTop: '10px'}}>
                <a href="https://github.com/" target="_blank">Github</a> |&nbsp;
                <a href="https://instagram.com" target="_blank">Instagram</a>
            </div>
        </div>
    )
}

export default Footer;