import Link from "next/link"

function Footer() {
    return (
        <footer className="footer-container">
            <div className="footer-links">
                <Link href="/">Home</Link>
                <span>|</span>
                <Link href="/about">About</Link>
                <span>|</span>
                <Link href="/contact">Contact</Link>
            </div>
            
            <div className="footer-info">
                <p>📞 010-1234-5678 | ✉️ example1@gmail.com</p>
                <p>© 2025 Bidus Inc. All rights reserved.</p>
            </div>
            
            <div className="footer-social">
                <a href="https://github.com/" target="_blank" rel="noopener noreferrer">
                    Github
                </a>
                <span>|</span>
                <a href="https://www.notion.com/" target="_blank" rel="noopener noreferrer">
                    Notion
                </a>
            </div>
        </footer>
    );
}

export default Footer;