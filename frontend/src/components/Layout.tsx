import { type ReactNode } from "react";
import { NavLink } from "react-router-dom";
import {
  LogOut,
  Home,
  ShoppingCart,
  Package,
  Users2,
  LineChart,
  Menu,
} from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { Sheet, SheetContent, SheetTrigger } from "./ui/sheet";

import { ModeToggle } from "./mode-toggle";
import { CartButton } from "./CartButton";


const NavLinks = () => {
  const { user } = useAuth();

  const hasRole = (role: string) =>
    user?.roles?.some((userRole) => userRole.replace("ROLE_", "") === role);

  const navLinkClass = ({ isActive }: { isActive: boolean }) =>
    `flex items-center gap-3 rounded-lg px-3 py-2 transition-all hover:text-primary ${
      isActive ? "bg-muted text-primary" : "text-muted-foreground"
    }`;

  return (
    <nav className="grid items-start gap-2 text-sm font-medium">
      {/* Links Comuns a Todos */}
      <NavLink to="/dashboard" className={navLinkClass}>
        <Home className="h-4 w-4" />
        {hasRole("CLIENTE") ? "Home" : "Dashboard"}
      </NavLink>

      {/* Links para Vendedor e Admin */}
      {(hasRole("VENDEDOR") || hasRole("ADMINISTRADOR")) && (
        <NavLink to="/pedidos" className={navLinkClass}>
          <ShoppingCart className="h-4 w-4" />
          Pedidos
        </NavLink>
      )}

      {/* Links para Cliente */}
      {hasRole("CLIENTE") && (
        <NavLink to="/meus-pedidos" className={navLinkClass}>
          <ShoppingCart className="h-4 w-4" />
          Meus Pedidos
        </NavLink>
      )}

      {/* Link de Produtos */}
      <NavLink to="/produtos" className={navLinkClass}>
        <Package className="h-4 w-4" />
        Produtos
      </NavLink>

      {/* Links Apenas para Admin */}
      {hasRole("ADMINISTRADOR") && (
        <>
          <NavLink to="/usuarios" className={navLinkClass}>
            <Users2 className="h-4 w-4" />
            Usuários
          </NavLink>
          <NavLink to="/relatorios" className={navLinkClass}>
            <LineChart className="h-4 w-4" />
            Relatórios
          </NavLink>
        </>
      )}
    </nav>
  );
};

export const Layout = ({ children }: { children: ReactNode }) => {
  const { user, logout } = useAuth();
  return (
    <div className="flex min-h-screen w-full flex-col bg-muted/40">
        {/* Sidebar para Desktop */}
        <aside className="fixed inset-y-0 left-0 z-10 hidden w-60 flex-col border-r bg-background sm:flex">
            <div className="flex h-14 items-center border-b px-4 lg:h-[60px] lg:px-6">
                <NavLink to="/dashboard" className="flex items-center gap-2 font-semibold">
                    <Package className="h-6 w-6" />
                    <span>Gerenciador de vendas</span>
                </NavLink>
            </div>
            <nav className="flex-1 space-y-2 p-2 font-medium">
               <NavLinks />
            </nav>
        </aside>

        <div className="flex flex-col sm:pl-60">
            {/* Header para Mobile e Desktop */}
            <header className="sticky top-0 z-30 flex h-14 items-center gap-4 border-b bg-background px-4 sm:static sm:h-auto sm:border-0 sm:bg-transparent sm:px-6 lg:h-[60px]">
                {/* Menu Gaveta para Mobile */}
                <Sheet>
                    <SheetTrigger asChild>
                        <Button size="icon" variant="outline" className="sm:hidden">
                            <Menu className="h-5 w-5" />
                            <span className="sr-only">Abrir menu</span>
                        </Button>
                    </SheetTrigger>
                    <SheetContent side="left" className="sm:max-w-xs">
                        <nav className="grid gap-6 text-lg font-medium">
                            <NavLink to="/dashboard" className="group flex h-10 w-10 shrink-0 m-4 items-center justify-center gap-2 rounded-full bg-primary text-lg font-semibold text-primary-foreground md:text-base">
                                <Package className="h-5 w-5 transition-all group-hover:scale-110" />
                                <span className="sr-only">Gerenciador de vendas</span>
                            </NavLink>
                            <NavLinks />
                        </nav>
                    </SheetContent>
                </Sheet>

                <div className="relative ml-auto flex items-center gap-2 md:grow-0">
                     <ModeToggle />
                     <CartButton />
                    <span className="text-sm text-muted-foreground hidden md:inline-block">{user?.name || user?.sub}</span>
                     <Button variant="outline" size="icon" onClick={logout}>
                        <LogOut className="h-4 w-4" />
                        <span className="sr-only">Sair</span>
                    </Button>
                </div>
            </header>
            <main className="flex-1 p-4 sm:px-6">
                {children}
            </main>
        </div>
    </div>
);
};