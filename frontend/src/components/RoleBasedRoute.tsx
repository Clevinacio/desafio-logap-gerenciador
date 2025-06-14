import { Outlet } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";
import AcessoNegadoPage from "@/pages/AcessoNegadoPage";

interface RoleBasedRouteProps {
  allowedRoles: string[];
}

/**
 * Este componente atua como um guardião de rotas baseado no perfil (role) do usuário.
 */
const RoleBasedRoute = ({ allowedRoles }: RoleBasedRouteProps) => {
  const { user } = useAuth();

  // Remove o prefixo "ROLE_" para facilitar a verificação
  const userRoles = user?.roles?.map((role) => role.replace("ROLE_", "")) || [];

  // Verifica se o usuário tem pelo menos um dos perfis permitidos
  const isAuthorized = userRoles.some((role) => allowedRoles.includes(role));

  if (!isAuthorized) {
    // Se não estiver autorizado, renderiza a página de Acesso Negado
    return <AcessoNegadoPage />;
  }

  // Se estiver autorizado, renderiza o componente filho da rota
  return <Outlet />;
};

export default RoleBasedRoute;
