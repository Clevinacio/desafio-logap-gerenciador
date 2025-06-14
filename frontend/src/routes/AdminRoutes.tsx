import { Route } from "react-router-dom";
import UsuariosPage from "@/pages/UsuariosPage";
import RelatoriosPage from "@/pages/RelatoriosPage";
import RoleBasedRoute from "@/components/RoleBasedRoute";
import { USER_ROLES } from "@/config/constants";

export const AdminRoutes = () => (
  <Route element={<RoleBasedRoute allowedRoles={[USER_ROLES.ADMINISTRADOR]} />}>
    <Route path="/usuarios" element={<UsuariosPage />} />
    <Route path="/relatorios" element={<RelatoriosPage />} />
  </Route>
);
