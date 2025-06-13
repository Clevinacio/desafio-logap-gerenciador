import { useState, useEffect } from "react";
import toast from "react-hot-toast";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import api from "@/services/api";
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
  statusPedido?: StatusPedido;
};

// --- Componente Principal da Página ---
const MeusPedidosPage = () => {
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

  useEffect(() => {
    const fetchPedidos = async () => {
      setIsLoading(true);
      try {
        const response = await api.get("/pedidos");
        setPedidos(response.data);
      } catch (error) {
        console.error("Falha ao buscar seus pedidos", error);
        toast.error("Não foi possível carregar seus pedidos.");
      } finally {
        setIsLoading(false);
      }
    };
    fetchPedidos();
  }, []);
  const getStatusVariant = (
    statusPedido: StatusPedido | undefined
  ): "default" | "secondary" | "destructive" | "outline" => {
    if (!statusPedido) return "outline";

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
    return <p>Carregando seus pedidos...</p>;
  }

  const handleRowClick = (pedidoId: number) => {
    navigate(`/pedidos/${pedidoId}`);
  };

  return (
    <div className="flex flex-col gap-4">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Meus Pedidos</h2>
        <p className="text-muted-foreground">
          Acompanhe o histórico e o status dos seus pedidos.
        </p>
      </div>
      <Card>
        <CardHeader>
          <CardTitle>Histórico de Compras</CardTitle>
          <CardDescription>
            Um total de {pedidos.length} pedidos encontrados.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Nº do Pedido</TableHead>
                <TableHead>Data</TableHead>
                <TableHead>Status</TableHead>
                <TableHead className="text-right">Valor Total</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {pedidos.length > 0 ? (
                pedidos.map((pedido) => (
                  <TableRow key={pedido.id} onClick={() => handleRowClick(pedido.id)}>
                    <TableCell className="font-medium">#{pedido.id}</TableCell>
                    <TableCell>
                      {formatadorData.format(new Date(pedido.dataCriacao))}
                    </TableCell>
                    <TableCell>
                      <Badge variant={getStatusVariant(pedido.statusPedido)}>
                        {pedido.statusPedido?.replace(/_/g, " ") ||
                          "STATUS INDEFINIDO"}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-right">
                      {formatadorMoeda.format(pedido.valorTotal)}
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={4} className="text-center h-24">
                    Você ainda não fez nenhum pedido. Vá para produtos e comece
                    a comprar!
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

export default MeusPedidosPage;
