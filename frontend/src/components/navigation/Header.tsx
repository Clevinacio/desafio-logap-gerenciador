import { NavLink } from "react-router-dom";
import { LogOut, Menu, Package } from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { ModeToggle } from "@/components/mode-toggle";
import { CartButton } from "@/components/CartButton";
import { NavLinks } from "./NavLinks";
import { ROUTES } from "@/config/constants";

export const Header = () => {
  const { user, logout } = useAuth();

  return (
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
            <NavLink 
              to={ROUTES.dashboard} 
              className="group flex h-10 w-10 shrink-0 m-4 items-center justify-center gap-2 rounded-full bg-primary text-lg font-semibold text-primary-foreground md:text-base"
            >
              <Package className="h-5 w-5 transition-all group-hover:scale-110" />
              <span className="sr-only">Gerenciador de vendas</span>
            </NavLink>
            <NavLinks isMobile />
          </nav>
        </SheetContent>
      </Sheet>

      <div className="relative ml-auto flex items-center gap-2 md:grow-0">
        <ModeToggle />
        <CartButton />
        <span className="text-sm text-muted-foreground hidden md:inline-block">
          {user?.name || user?.sub}
        </span>
        <Button variant="outline" size="icon" onClick={logout}>
          <LogOut className="h-4 w-4" />
          <span className="sr-only">Sair</span>
        </Button>
      </div>
    </header>
  );
};
