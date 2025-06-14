import { Routes, Route } from "react-router-dom";
import HomePage from "@/pages/HomePage";
import ProdutosRouter from "@/pages/routers/ProdutosRouter";
import { ClienteRoutes } from "./ClienteRoutes";
import { VendedorRoutes } from "./VendedorRoutes";
import { AdminRoutes } from "./AdminRoutes";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const ProtectedRoutes = () => (
  <Routes>
    <Route path="/dashboard" element={<HomePage />} />
    <Route path="/produtos" element={<ProdutosRouter />} />
    
    <ClienteRoutes />
    <VendedorRoutes />
    <AdminRoutes />
    
    <Route
      path="*"
      element={<PlaceholderPage title="Página não encontrada" />}
    />
  </Routes>
);
