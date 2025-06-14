import { useState, useEffect, useCallback } from "react";
import { useParams, Link } from "react-router-dom";
import toast from "react-hot-toast";
import api from "../services/api";
import { useAuth } from "../hooks/useAuth";

import { Button } from "../components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
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
import { ArrowLeft } from "lucide-react";
import { isAxiosError } from "axios";

// --- Tipos ---
type StatusPedido =
  | "PENDENTE_APROVACAO"
  | "EM_ANDAMENTO"
  | "FINALIZADO"
  | "CANCELADO";

interface ItemPedido {
  produtoId: number;
  nomeProduto: string;
  quantidade: number;
  precoUnitario: number;
}

interface PedidoDetalhado {
  id: number;
  nomeCliente: string;
  emailCliente: string;
  status: StatusPedido;
  valorTotal: number;
  dataCriacao: string;
  itens: ItemPedido[];
}

// --- Componente Principal ---
const PedidoDetalhadoPage = () => {
  const { id } = useParams<{ id: string }>();
  const { user } = useAuth();
  const [pedido, setPedido] = useState<PedidoDetalhado | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const formatadorMoeda = new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
  const formatadorData = new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "long",
    timeStyle: "short",
  });

  const isGestor = user?.roles?.some(
    (role) => role.includes("ADMINISTRADOR") || role.includes("VENDEDOR")
  );
  const fetchPedido = useCallback(async () => {
    setIsLoading(true);
    try {
      const response = await api.get(`/pedidos/${id}`);
      setPedido(response.data);
    } catch (error) {
      console.error("Falha ao buscar detalhes do pedido", error);
      toast.error("Não foi possível carregar os detalhes do pedido.");
      setPedido(null);
    } finally {
      setIsLoading(false);
    }
  }, [id]);
  useEffect(() => {
    if (id) {
      fetchPedido();
    }
  }, [id, fetchPedido]);

  const handleUpdateStatus = async (novoStatus: StatusPedido) => {
    toast.loading(`Alterando status para ${novoStatus.replace(/_/g, " ")}...`, {
      id: "status-update",
    });
    try {
      await api.patch(`/pedidos/${id}/status`, { novoStatus });
      toast.success("Status alterado com sucesso!", { id: "status-update" });
      fetchPedido();
    } catch (error) {
      if(isAxiosError(error) && error.response) {
        const errorMessage = error.response.data?.message || "Erro ao atualizar status";
        toast.error(errorMessage, { id: "status-update" });
      }
    }
  };
  const getStatusVariant = (
    status?: StatusPedido
  ): "default" | "secondary" | "destructive" | "outline" => {
    switch (status) {
      case "PENDENTE_APROVACAO":
        return "outline";
      case "EM_ANDAMENTO":
        return "secondary";
      case "FINALIZADO":
        return "default";
      case "CANCELADO":
        return "destructive";
      default:
        return "outline";
    }
  };

  if (isLoading) return <p>Carregando detalhes do pedido...</p>;
  if (!pedido)
    return <p>Pedido não encontrado ou você não tem permissão para vê-lo.</p>;

  const canUpdateStatus =
    pedido.status !== "FINALIZADO" && pedido.status !== "CANCELADO";

  return (
    <div className="flex flex-col gap-6">
      <Link
        to={isGestor ? "/pedidos" : "/meus-pedidos"}
        className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground"
      >
        <ArrowLeft className="h-4 w-4" />
        Voltar para a lista de pedidos
      </Link>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        <Card>
          <CardHeader>
            <CardDescription>Status</CardDescription>
            <CardTitle>
              <Badge
                variant={getStatusVariant(pedido.status)}
                className="text-base"
              >
                {pedido.status.replace(/_/g, " ")}
              </Badge>
            </CardTitle>
          </CardHeader>
        </Card>
        <Card>
          <CardHeader>
            <CardDescription>Cliente</CardDescription>
            <CardTitle className="truncate">{pedido.nomeCliente}</CardTitle>
            <CardDescription>{pedido.emailCliente}</CardDescription>
          </CardHeader>
        </Card>
        <Card>
          <CardHeader>
            <CardDescription>Total</CardDescription>
            <CardTitle>{formatadorMoeda.format(pedido.valorTotal)}</CardTitle>
          </CardHeader>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Itens do Pedido</CardTitle>
          <CardDescription>
            Realizado em {formatadorData.format(new Date(pedido.dataCriacao))}
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Produto</TableHead>
                <TableHead className="text-center">Quantidade</TableHead>
                <TableHead className="text-right">Preço Unitário</TableHead>
                <TableHead className="text-right">Subtotal</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {pedido.itens.map((item, index) => (
                <TableRow key={`${item.produtoId}-${index}`}>
                  <TableCell className="font-medium">
                    {item.nomeProduto}
                  </TableCell>
                  <TableCell className="text-center">
                    {item.quantidade}
                  </TableCell>
                  <TableCell className="text-right">
                    {formatadorMoeda.format(item.precoUnitario)}
                  </TableCell>
                  <TableCell className="text-right">
                    {formatadorMoeda.format(
                      item.precoUnitario * item.quantidade
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
        {/* Botões de Ação para Gestores */}
        {isGestor && canUpdateStatus && (
          <CardFooter className="border-t pt-6 justify-end gap-2">
            <Button
              variant="outline"
              onClick={() => handleUpdateStatus("CANCELADO")}
            >
              Cancelar Pedido
            </Button>
            <Button onClick={() => handleUpdateStatus("FINALIZADO")}>
              Finalizar Pedido
            </Button>
          </CardFooter>
        )}
      </Card>
    </div>
  );
};

export default PedidoDetalhadoPage;
