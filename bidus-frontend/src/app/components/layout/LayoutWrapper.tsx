'use client';

import { useState } from "react";
import Sidebar from "../sidebar/Sidebar";
import Navbar from "../nav/Navbar";
import Footer from "../footer/Footer";

export default function LayoutWrapper({
    children,
}: {
    children: React.ReactNode;
}) {
    const [isSidebarOpen, setSidebarOpen] = useState(true);

    const toggleSidebar = () => {
        setSidebarOpen(!isSidebarOpen);
    };

    const mainContentClassName = `main-content ${isSidebarOpen ? '' : 'sidebar-collapsed'}`;

    return (
        <div className="layout-container">
            <Sidebar isOpen={isSidebarOpen} />
            <div style={{ display: 'flex', flexDirection: 'column', flexGrow: 1 }}>
                <Navbar onToggleSidebar={toggleSidebar} />
                <main className={mainContentClassName}>
                    {children}
                </main>
                <Footer/>
            </div>
        </div>
    );
}