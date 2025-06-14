import { Route } from "react-router-dom";
import MeusPedidosPage from "@/pages/MeusPedidosPage";
import PedidoDetalhadoPage from "@/pages/PedidoDetalhadoPage";
import CartPage from "@/pages/CartPage";
import PedidoConfirmadoPage from "@/pages/PedidoConfirmado";
import RoleBasedRoute from "@/components/RoleBasedRoute";
import { USER_ROLES } from "@/config/constants";

export const ClienteRoutes = () => (
  <Route element={<RoleBasedRoute allowedRoles={[USER_ROLES.CLIENTE]} />}>
    <Route path="/meus-pedidos" element={<MeusPedidosPage />} />
    <Route path="/meus-pedidos/:id" element={<PedidoDetalhadoPage />} />
    <Route path="/carrinho" element={<CartPage />} />
    <Route path="/pedido-confirmado/:id" element={<PedidoConfirmadoPage />} />
  </Route>
);
