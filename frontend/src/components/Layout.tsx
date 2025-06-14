import type { ReactNode } from "react";
import { NavLink } from "react-router-dom";
import { Package } from "lucide-react";
import { Header } from "./navigation/Header";
import { NavLinks } from "./navigation/NavLinks";
import { APP_CONFIG } from "@/config/app.config";
import { ROUTES } from "@/config/constants";

export const Layout = ({ children }: { children: ReactNode }) => {
  return (
    <div className="flex min-h-screen w-full flex-col bg-muted/40">
      {/* Sidebar para Desktop */}
      <aside className="fixed inset-y-0 left-0 z-10 hidden w-60 flex-col border-r bg-background sm:flex">
        <div className="flex h-14 items-center border-b px-4 lg:h-[60px] lg:px-6">
          <NavLink to={ROUTES.root} className="flex items-center gap-2 font-semibold">
            <Package className="h-6 w-6" />
            <span>{APP_CONFIG.app.name}</span>
          </NavLink>
        </div>
        <nav className="flex-1 space-y-2 p-2 font-medium">
          <NavLinks />
        </nav>
      </aside>

      <div className="flex flex-col sm:pl-60">
        <Header />
        <main className="flex-1 p-4 sm:px-6">
          {children}
        </main>
      </div>
    </div>
  );
};