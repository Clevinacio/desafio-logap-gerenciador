import { Route, Routes } from "react-router-dom";
import "./App.css";
import { Toaster } from "react-hot-toast";
import LoginPage from "./pages/LoginPage";
import ProtectedRoute from "./components/ProtectedRoute";
import { Layout } from "./components/Layout";
import { ProtectedRoutes } from "./routes/ProtectedRoutes";
import { APP_CONFIG } from "./config/app.config";
import { ROUTES } from "./config/constants";

function App() {
  return (
    <>
      <Toaster 
        position={APP_CONFIG.ui.toast.position} 
        reverseOrder={APP_CONFIG.ui.toast.reverseOrder} 
      />
      <Routes>
        <Route path={ROUTES.login} element={<LoginPage />} />
        <Route path={ROUTES.root} element={<LoginPage />} />

        <Route element={<ProtectedRoute />}>
          <Route
            path="/*"
            element={
              <Layout>
                <ProtectedRoutes />
              </Layout>
            }
          />
        </Route>
      </Routes>
    </>
  );
}

export default App;
