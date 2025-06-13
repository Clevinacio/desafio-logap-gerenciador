import { useState, useEffect } from "react";
import toast from "react-hot-toast";
import { Bar, BarChart, CartesianGrid, XAxis, Pie, PieChart, Cell, YAxis } from "recharts";
import { DollarSign, ShoppingCart, Users } from "lucide-react";

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "../components/ui/card";
import {
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
  ChartLegend,
  ChartLegendContent,
} from "../components/ui/chart";
import api from "../services/api";

// --- Tipagem dos Dados ---
interface TopProduct {
  nomeProduto: string;
  totalVendido: number;
}

interface ActiveCustomer {
  nomeCliente: string;
  totalPedidos: number;
}

interface DashboardStats {
  faturamentoTotal: number;
  totalPedidos: number;
  pedidosPendentes: number;
  topProdutos: TopProduct[];
  topClientes: ActiveCustomer[];
}

const formatadorMoeda = new Intl.NumberFormat("pt-BR", {
  style: "currency",
  currency: "BRL",
});

const chartColors = [
  "hsl(12, 76%, 61%)", 
  "hsl(173, 58%, 39%)", 
  "hsl(197, 37%, 24%)",  
  "hsl(43, 74%, 66%)",   
  "hsl(27, 87%, 67%)",
];

const RelatoriosPage = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      setIsLoading(true);
      try {
        const response = await api.get("/dashboard/stats");
        setStats(response.data);
      } catch (error) {
        console.error("Falha ao buscar estatísticas", error);
        toast.error("Não foi possível carregar os dados do dashboard.");
      } finally {
        setIsLoading(false);
      }
    };
    fetchStats();
  }, []);

  if (isLoading) {
    return <p>Carregando relatórios...</p>;
  }
  if (!stats) {
    return (
      <p>Não foi possível carregar os dados. Tente novamente mais tarde.</p>
    );
  }
  
  // Configuração para a legenda do gráfico de pizza
  const pieChartConfig = stats.topClientes.reduce(
    (acc, cliente) => {
      acc[cliente.nomeCliente] = { label: cliente.nomeCliente };
      return acc;
    },
    {} as Record<string, { label: string }>
  );

  return (
    <div className="flex flex-col gap-4">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Dashboard Gerencial</h2>
        <p className="text-muted-foreground">Visão geral do desempenho da sua loja.</p>
      </div>

      {/* Cards Principais */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Faturamento Total</CardTitle>
            <DollarSign className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{formatadorMoeda.format(stats.faturamentoTotal)}</div>
            <p className="text-xs text-muted-foreground">Baseado em pedidos finalizados.</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total de Pedidos</CardTitle>
            <ShoppingCart className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">+{stats.totalPedidos}</div>
            <p className="text-xs text-muted-foreground">Desde o início das operações.</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Pedidos Pendentes</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.pedidosPendentes}</div>
            <p className="text-xs text-muted-foreground">Aguardando aprovação.</p>
          </CardContent>
        </Card>
      </div>

      {/* Gráficos */}
      <div className="grid grid-cols-1 gap-4 lg:grid-cols-7">
        <Card className="col-span-1 lg:col-span-4">
          <CardHeader>
            <CardTitle>Top 5 Produtos Mais Vendidos</CardTitle>
            <CardDescription>Quantidade total de unidades vendidas.</CardDescription>
          </CardHeader>
          <CardContent className="pl-2">
            <ChartContainer config={{}} className="h-[350px] w-full">
              <BarChart
                accessibilityLayer
                data={stats.topProdutos}
                layout="vertical"
                margin={{ left: 10, right: 30 }}
              >
                <CartesianGrid horizontal={false} />
                <XAxis type="number" hide />
                <YAxis
                  dataKey="nomeProduto"
                  type="category"
                  tickLine={false}
                  tickMargin={10}
                  axisLine={false}
                  tickFormatter={(value) => value.slice(0, 12) + (value.length > 12 ? "..." : "")}
                  width={100}
                />
                <ChartTooltip
                  cursor={false}
                  content={<ChartTooltipContent indicator="line" />}
                />
                <Bar dataKey="totalVendido" layout="vertical" radius={5}>
                  {stats.topProdutos.map((_, index) => (
                    <Cell key={`cell-${index}`} fill={chartColors[index % chartColors.length]} />
                  ))}
                </Bar>
              </BarChart>
            </ChartContainer>
          </CardContent>
        </Card>
        <Card className="col-span-1 lg:col-span-3 flex flex-col">
          <CardHeader>
            <CardTitle>Clientes Mais Ativos</CardTitle>
            <CardDescription>Distribuição de pedidos por cliente.</CardDescription>
          </CardHeader>          <CardContent className="flex flex-1 items-center justify-center pb-0">
            <ChartContainer
              config={pieChartConfig}
              className="mx-auto aspect-square max-h-[300px] w-full"
            >
              <PieChart width={300} height={300}>
                <ChartTooltip
                  cursor={false}
                  content={<ChartTooltipContent hideLabel />}
                />
                <Pie
                  data={stats.topClientes}
                  dataKey="totalPedidos"
                  nameKey="nomeCliente"
                  cx="50%"
                  cy="50%"
                  innerRadius={50}
                  outerRadius={100}
                  strokeWidth={2}
                >
                  {stats.topClientes.map((_, index) => (
                    <Cell 
                      key={`cell-${index}`} 
                      fill={chartColors[index % chartColors.length]} 
                    />
                  ))}
                </Pie>
                <ChartLegend
                  content={<ChartLegendContent nameKey="nomeCliente" />}
                  className="flex-wrap justify-center"
                />
              </PieChart>
            </ChartContainer>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default RelatoriosPage;
