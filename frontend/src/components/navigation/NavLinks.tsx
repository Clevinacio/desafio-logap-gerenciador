import { NavLink } from "react-router-dom";
import {
  Home,
  ShoppingCart,
  Package,
  Users2,
  LineChart,
} from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { roleUtils } from "@/utils/roleUtils";
import { ROUTES } from "@/config/constants";

interface NavLinksProps {
  isMobile?: boolean;
}

export const NavLinks = ({ isMobile = false }: NavLinksProps) => {
  const { user } = useAuth();

  const navLinkClass = ({ isActive }: { isActive: boolean }) => {
    const baseClass = "flex items-center gap-3 rounded-lg px-3 py-2 transition-all hover:text-primary";
    const activeClass = isActive ? "bg-muted text-primary" : "text-muted-foreground";
    const mobileClass = isMobile ? "text-lg" : "";
    
    return `${baseClass} ${activeClass} ${mobileClass}`;
  };

  return (
    <nav className={`grid items-start gap-2 text-sm font-medium ${isMobile ? 'gap-6 text-lg' : ''}`}>
      {/* Links Comuns a Todos */}
      <NavLink to={ROUTES.dashboard} className={navLinkClass}>
        <Home className="h-4 w-4" />
        {roleUtils.isCliente(user?.roles) ? "Home" : "Dashboard"}
      </NavLink>

      {/* Links para Vendedor e Admin */}
      {roleUtils.canManageOrders(user?.roles) && (
        <NavLink to={ROUTES.pedidos} className={navLinkClass}>
          <ShoppingCart className="h-4 w-4" />
          Pedidos
        </NavLink>
      )}

      {/* Links para Cliente */}
      {roleUtils.isCliente(user?.roles) && (
        <NavLink to={ROUTES.meusPedidos} className={navLinkClass}>
          <ShoppingCart className="h-4 w-4" />
          Meus Pedidos
        </NavLink>
      )}

      {/* Link de Produtos */}
      <NavLink to={ROUTES.produtos} className={navLinkClass}>
        <Package className="h-4 w-4" />
        Produtos
      </NavLink>

      {/* Links Apenas para Admin */}
      {roleUtils.canAccessAdmin(user?.roles) && (
        <>
          <NavLink to={ROUTES.usuarios} className={navLinkClass}>
            <Users2 className="h-4 w-4" />
            Usuários
          </NavLink>
          <NavLink to={ROUTES.relatorios} className={navLinkClass}>
            <LineChart className="h-4 w-4" />
            Relatórios
          </NavLink>
        </>
      )}
    </nav>
  );
};
