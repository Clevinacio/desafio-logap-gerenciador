import { useAuth } from "@/hooks/useAuth";
import ProdutosPage from "@/pages/ProdutosPage"; // Visão de Gestão
import VitrinePage from "@/pages/VitrinePage"; // Visão de Vitrine

/**
 * Este componente atua como um roteador condicional.
 * Ele renderiza a página de gestão de produtos para administradores/vendedores
 * e a página de vitrine para clientes.
 */
const ProdutosRouter = () => {
  const { user } = useAuth();
  const isAdminOrVendedor = user?.roles?.some(
    (role) => role.includes("ADMINISTRADOR") || role.includes("VENDEDOR")
  );

  return isAdminOrVendedor ? <ProdutosPage /> : <VitrinePage />;
};

export default ProdutosRouter;
