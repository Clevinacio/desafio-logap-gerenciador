import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";
import api from "@/services/api";
import { type Produto } from "@/types/produto";
import { ProdutoCard } from "@/components/ProdutoCard";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { LineChart, Package, ShoppingCart, Users2 } from "lucide-react";

const ClienteDashboard = () => {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  useEffect(() => {
    const fetchProdutos = async () => {
      try {
        const response = await api.get("/produtos");
        setProdutos(response.data.content.slice(0, 4));
      } catch (error) {
        console.error("Falha ao buscar produtos", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchProdutos();
  }, []);

  if (isLoading) {
    return <p>Carregando produtos...</p>;
  }

  return (
    <div className="flex flex-col gap-8">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">
          Seja bem-vindo(a) à nossa loja!
        </h2>
        <p className="text-muted-foreground">
          Confira alguns de nossos produtos em destaque.
        </p>
      </div>

      {produtos.length > 0 ? (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          {produtos.map((produto) => (
            <ProdutoCard key={produto.id} produto={produto} />
          ))}
        </div>
      ) : (
        <p>Nenhum produto cadastrado no momento. Volte em breve!</p>
      )}

      <div className="flex justify-center">
        <Button asChild>
          <Link to="/produtos">Ver todos os produtos</Link>
        </Button>
      </div>
    </div>
  );
};

// --- Visão do Admin/Vendedor ---
const GestorDashboard = () => {
  const { user } = useAuth();
  const isAdmin = user?.roles?.some((role) => role.includes("ADMINISTRADOR"));

  return (
    <div className="flex flex-col gap-4">
      <h2 className="text-3xl font-bold tracking-tight">Olá, {user?.name}!</h2>
      <p className="text-muted-foreground">
        Utilize os atalhos abaixo para gerenciar a loja.
      </p>
      <div className="grid gap-4 md:grid-cols-2">
        <Link to="/pedidos">
          <Card className="hover:bg-muted">
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium">
                Gerenciar Pedidos
              </CardTitle>
              <ShoppingCart className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardDescription className="p-6 pt-0">
              Acompanhe, aprove ou cancele os pedidos dos clientes.
            </CardDescription>
          </Card>
        </Link>
        <Link to="/produtos">
          <Card className="hover:bg-muted">
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium">
                Gerenciar Produtos
              </CardTitle>
              <Package className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardDescription className="p-6 pt-0">
              Adicione novos produtos e atualize o estoque.
            </CardDescription>
          </Card>
        </Link>
        {isAdmin && (
          <>
            <Link to="/usuarios">
              <Card className="hover:bg-muted transition-colors">
                <CardHeader className="flex flex-row items-center justify-between pb-2">
                  <CardTitle className="text-sm font-medium">
                    Gestão de Usuários
                  </CardTitle>
                  <Users2 className="h-4 w-4 text-muted-foreground" />
                </CardHeader>
                <CardDescription className="p-6 pt-0">
                  Adicione, edite ou remova usuários do sistema.
                </CardDescription>
              </Card>
            </Link>
            <Link to="/relatorios">
              <Card className="hover:bg-muted transition-colors">
                <CardHeader className="flex flex-row items-center justify-between pb-2">
                  <CardTitle className="text-sm font-medium">
                    Relatórios
                  </CardTitle>
                  <LineChart className="h-4 w-4 text-muted-foreground" />
                </CardHeader>
                <CardDescription className="p-6 pt-0">
                  Visualize o desempenho de vendas e clientes.
                </CardDescription>
              </Card>
            </Link>
          </>
        )}
      </div>
    </div>
  );
};

// --- Componente Principal da Página ---
const HomePage = () => {
  const { user } = useAuth();
  const isAdminOrVendedor = user?.roles?.some(
    (role) => role.includes("ADMINISTRADOR") || role.includes("VENDEDOR")
  );

  return isAdminOrVendedor ? <GestorDashboard /> : <ClienteDashboard />;
};

export default HomePage;
