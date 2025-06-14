import { Route } from "react-router-dom";
import PedidosPage from "@/pages/PedidosPage";
import PedidoDetalhadoPage from "@/pages/PedidoDetalhadoPage";
import RoleBasedRoute from "@/components/RoleBasedRoute";
import { USER_ROLES } from "@/config/constants";

export const VendedorRoutes = () => (
  <Route element={<RoleBasedRoute allowedRoles={[USER_ROLES.VENDEDOR, USER_ROLES.ADMINISTRADOR]} />}>
    <Route path="/pedidos" element={<PedidosPage />} />
    <Route path="/pedidos/:id" element={<PedidoDetalhadoPage />} />
  </Route>
);
