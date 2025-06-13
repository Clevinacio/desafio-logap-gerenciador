import { Route, Routes } from "react-router-dom";
import "./App.css";
import LoginPage from "./pages/LoginPage";
import ProtectedRoute from "./components/ProtectedRoute";
import HomePage from "./pages/HomePage";
import { Layout } from "./components/Layout";
import UsuariosPage from "./pages/UsuariosPage";
import { Toaster } from "react-hot-toast";
import PedidosPage from "./pages/PedidosPage";
import ProdutosRouter from "./pages/routers/ProdutosRouter";
import CartPage from "./pages/CartPage";
import PedidoConfirmadoPage from "./pages/PedidoConfirmado";
import MeusPedidosPage from "./pages/MeusPedidosPage";
import PedidoDetalhadoPage from "./pages/PedidoDetalhadoPage";
import RelatoriosPage from "./pages/RelatoriosPage";

const PlaceholderPage = ({ title }: { title: string }) => (
  <div>
    <h2 className="text-3xl font-bold tracking-tight">{title}</h2>
    <p className="text-muted-foreground">Página em construção.</p>
  </div>
);

function App() {
  return (
    <>
      <Toaster position="top-center" reverseOrder={false} />
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<LoginPage />} />

        <Route element={<ProtectedRoute />}>
          <Route
            path="/*"
            element={
              <Layout>
                <Routes>
                  <Route path="/dashboard" element={<HomePage />} />
                  <Route path="/pedidos" element={<PedidosPage />} />
                  <Route
                    path="/pedidos/:id"
                    element={<PedidoDetalhadoPage />}
                  />
                  <Route path="/meus-pedidos" element={<MeusPedidosPage />} />
                  <Route
                    path="/meus-pedidos/:id"
                    element={<PedidoDetalhadoPage />}
                  />
                  <Route path="/produtos" element={<ProdutosRouter />} />
                  <Route path="/usuarios" element={<UsuariosPage />} />
                  <Route
                    path="/relatorios"
                    element={<RelatoriosPage />}
                  />
                  <Route path="/carrinho" element={<CartPage />} />
                  <Route
                    path="/pedido-confirmado/:id"
                    element={<PedidoConfirmadoPage />}
                  />
                  <Route
                    path="*"
                    element={<PlaceholderPage title="Página não encontrada" />}
                  />
                </Routes>
              </Layout>
            }
          />
        </Route>
      </Routes>
    </>
  );
}

export default App;
