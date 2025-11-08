import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { IoHeadset } from "react-icons/io5";
import { LuBookmarkCheck, LuLayoutDashboard, LuLibraryBig, LuSettings } from "react-icons/lu";
import { MdOutlineSell } from "react-icons/md";
import { SidebarProps } from '../../../types/others/sidebar';

function Sidebar({ isOpen }: SidebarProps) {
    const menuItems = [
        { 
            href: "/dashboard", 
            label: "대시보드", 
            icon: LuLayoutDashboard,
        },
        { 
            href: "/library", 
            label: "보관함", 
            icon: LuLibraryBig,
        },
        { 
            href: "/bookmark", 
            label: "즐겨찾기", 
            icon: LuBookmarkCheck, 
        },
        { 
            href: "/sales", 
            label: "상품판매", 
            icon: MdOutlineSell,
        },
        { 
            href: "/setting", 
            label: "개인설정", 
            icon: LuSettings,
        },
        { 
            href: "/customer-service-center", 
            label: "고객센터", 
            icon: IoHeadset,
        },
    ];
    const pathname = usePathname();
    const sidebarClassName = `sidebar ${isOpen ? '' : 'collapsed'}`;
    const bottomItemHrefs = ['/customer-service-center'];

    const mainMenuItems = menuItems.filter(
        (item) => !bottomItemHrefs.includes(item.href)
    );

    const bottomMenuItems = menuItems.filter((item) =>
        bottomItemHrefs.includes(item.href)
    );

    const renderMenuItems = (items: typeof menuItems) => {
        return items.map((item) => {
            const Icon = item.icon;
            const isActive = pathname === item.href;

            return (
                <li key={item.href} className={isActive ? 'active' : ''}>
                    <Link href={item.href}>
                        <Icon className="menu-icon" />
                        {item.label}
                    </Link>
                </li>
            );
        });
    };

    return (
        <aside className={sidebarClassName}>
            <nav className='sidebar-nav'>
                <ul className="sidebar-menu">{renderMenuItems(mainMenuItems)}</ul>
                <ul className="sidebar-menu sidebar-menu-bottom">{renderMenuItems(bottomMenuItems)}</ul>
            </nav>
        </aside>
    );
}

export default Sidebar;