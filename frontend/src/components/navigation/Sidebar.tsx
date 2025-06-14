import { NavLink } from "react-router-dom";
import { Package } from "lucide-react";
import { APP_CONFIG } from "@/config/app.config";
import { ROUTES } from "@/config/constants";

export const Sidebar = () => {
  return (
    <aside className="fixed inset-y-0 left-0 z-10 hidden w-60 flex-col border-r bg-background sm:flex">
      <div className="flex h-14 items-center border-b px-4 lg:h-[60px] lg:px-6">
        <NavLink to={ROUTES.root} className="flex items-center gap-2 font-semibold">
          <Package className="h-6 w-6" />
          <span>{APP_CONFIG.app.name}</span>
        </NavLink>
      </div>
      <nav className="flex-1 space-y-2 p-2 font-medium">
        {/* NavLinks será importado do componente pai */}
      </nav>
    </aside>
  );
};
