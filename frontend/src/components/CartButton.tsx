import { useCart } from "@/hooks/useCart";
import { ShoppingCart } from "lucide-react";
import { Button } from "./ui/button";
import { Link } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";

export const CartButton = () => {
  const { cartCount } = useCart();
  const { user } = useAuth();

  if (!user?.roles?.includes("ROLE_CLIENTE")) {
    return null;
  }
  return (
    <Link to="/carrinho">
      <Button variant="outline" size="icon" className="relative">
        <ShoppingCart className="h-4 w-4" />
        {cartCount > 0 && (
          <span className="absolute -top-1 -right-1 flex h-4 w-4 items-center justify-center rounded-full bg-primary text-xs text-primary-foreground">
            {cartCount}
          </span>
        )}
        <span className="sr-only">Carrinho</span>
      </Button>
    </Link>
  );
};
