import { useCart } from "@/hooks/useCart";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardFooter,
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
import { Input } from "@/components/ui/input";
import { Trash2 } from "lucide-react";
import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import toast from "react-hot-toast";
import api from "@/services/api";
import { isAxiosError } from "axios";

const CartPage = () => {
  const { cartItems, updateQuantity, removeItem, cartTotal, clearCart } =
    useCart();
  const formatadorMoeda = new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();


  const handleFinalizarPedido = async () => {
    setIsLoading(true);
  
    toast.loading("Enviando seu pedido...", { id: "checkout" });

    try {
      const pedidoRequest = {
        itens: cartItems.map((item) => ({
          produtoId: item.id,
          quantidade: item.quantity,
        })),
      };

      const response = await api.post("/pedidos", pedidoRequest);

      toast.success("Pedido realizado com sucesso!", { id: "checkout" });
      clearCart();

      // Acessa o ID do pedido a partir da resposta da API
      const pedidoId = response.data.id;
      navigate(`/pedido-confirmado/${pedidoId}`);
    } catch (err) {
      if (isAxiosError(err)) {
        toast.error(
          err.response?.data?.message || "Não foi possível finalizar o pedido.",
          { id: "checkout" }
        );
      } else {
        toast.error("Ocorreu um erro inesperado.", { id: "checkout" });
      }
    } finally {
      setIsLoading(false);
    }
  };

  if (cartItems.length === 0) {
    return (
      <div className="text-center py-16">
        <h2 className="text-2xl font-semibold">Seu carrinho está vazio</h2>
        <p className="text-muted-foreground mt-2">
          Adicione produtos da nossa vitrine para começar.
        </p>
        <Button asChild className="mt-4">
          <Link to="/produtos">Ver Produtos</Link>
        </Button>
      </div>
    );
  }

  return (
    <div className="grid gap-8 md:grid-cols-3">
      <div className="md:col-span-2">
        <Card>
          <CardHeader>
            <CardTitle>Seu Carrinho</CardTitle>
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Produto</TableHead>
                  <TableHead>Quantidade</TableHead>
                  <TableHead className="text-right">Total</TableHead>
                  <TableHead>
                    <span className="sr-only">Remover</span>
                  </TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {cartItems.map((item) => (
                  <TableRow key={item.id}>
                    <TableCell>{item.nome}</TableCell>
                    <TableCell>
                      <Input
                        type="number"
                        min="1"
                        value={item.quantity}
                        onChange={(e) =>
                          updateQuantity(item.id, parseInt(e.target.value, 10))
                        }

                        className="w-20"
                        disabled={isLoading}
                      />
                    </TableCell>
                    <TableCell className="text-right">
                      {formatadorMoeda.format(item.preco * item.quantity)}
                    </TableCell>
                    <TableCell className="text-right">
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => removeItem(item.id)}
                        disabled={isLoading}
                      >
                        <Trash2 className="h-4 w-4 text-destructive" />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </div>
      <div>
        <Card>
          <CardHeader>
            <CardTitle>Resumo do Pedido</CardTitle>
          </CardHeader>
          <CardContent className="grid gap-4">
            <div className="flex justify-between">
              <span>Subtotal</span>
              <span>{formatadorMoeda.format(cartTotal)}</span>
            </div>
            <div className="flex justify-between font-bold text-lg">
              <span>Total</span>
              <span>{formatadorMoeda.format(cartTotal)}</span>
            </div>
          </CardContent>
          <CardFooter className="flex flex-col gap-2">
            <Button className="w-full" 
              onClick={handleFinalizarPedido}
              disabled={isLoading}
            >Finalizar Pedido</Button>
            <Button variant="outline" className="w-full" onClick={clearCart}>
              Limpar Carrinho
            </Button>
          </CardFooter>
        </Card>
      </div>
    </div>
  );
};

export default CartPage;
