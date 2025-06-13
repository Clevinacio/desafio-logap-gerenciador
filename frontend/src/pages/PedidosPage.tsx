import { useState, useEffect } from "react";
import toast from "react-hot-toast";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "../components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../components/ui/table";
import { Badge } from "../components/ui/badge";
import api from "../services/api";
import { useNavigate } from "react-router-dom";

// --- Tipos ---
type StatusPedido =
  | "PENDENTE_APROVACAO"
  | "EM_ANDAMENTO"
  | "FINALIZADO"
  | "CANCELADO";

type Pedido = {
  id: number;
  dataCriacao: string;
  valorTotal: number;
  statusPedido: StatusPedido;
  nomeCliente: string;
};

// --- Componente Principal da Página ---
const PedidosPage = () => {
  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  const formatadorMoeda = new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
  const formatadorData = new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short",
  });

  const fetchPedidos = async () => {
    setIsLoading(true);
    try {
      const response = await api.get("/pedidos");
      setPedidos(response.data);
    } catch (error) {
      console.error("Falha ao buscar pedidos", error);
      toast.error("Não foi possível carregar os pedidos.");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchPedidos();
  }, []);

  type BadgeVariant = "default" | "secondary" | "destructive" | "outline";

  const getStatusVariant = (statusPedido: StatusPedido): BadgeVariant => {
    switch (statusPedido) {
      case "PENDENTE_APROVACAO":
        return "secondary";
      case "EM_ANDAMENTO":
        return "default";
      case "FINALIZADO":
        return "outline";
      case "CANCELADO":
        return "destructive";
      default:
        return "outline";
    }
  };

  if (isLoading) {
    return <p>Carregando pedidos...</p>;
  }

  const handleRowClick = (pedidoId: number) => {
    navigate(`/pedidos/${pedidoId}`);
  };

  return (
    <div className="flex flex-col gap-4">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">
            Gestão de Pedidos
          </h2>
          <p className="text-muted-foreground">
            Acompanhe e gerencie todos os pedidos da loja.
          </p>
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Histórico de Pedidos</CardTitle>
          <CardDescription>
            Um total de {pedidos.length} pedidos encontrados.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Nº do Pedido</TableHead>
                <TableHead>Cliente</TableHead>
                <TableHead>Data</TableHead>
                <TableHead>Status</TableHead>
                <TableHead className="text-right">Valor Total</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {pedidos.length > 0 ? (
                pedidos.map((pedido) => (
                  <TableRow
                    key={pedido.id}
                    onClick={() => handleRowClick(pedido.id)}
                  >
                    <TableCell className="font-medium">#{pedido.id}</TableCell>
                    <TableCell>{pedido.nomeCliente}</TableCell>
                    <TableCell>
                      {formatadorData.format(new Date(pedido.dataCriacao))}
                    </TableCell>
                    <TableCell>
                      <Badge
                        variant={
                          getStatusVariant(pedido.statusPedido) as BadgeVariant
                        }
                      >
                        {pedido.statusPedido.replace(/_/g, " ")}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-right">
                      {formatadorMoeda.format(pedido.valorTotal)}
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={6} className="text-center h-24">
                    Nenhum pedido encontrado.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
};

export default PedidosPage;
