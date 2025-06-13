import { Button } from "./ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "./ui/card";
import { type Produto } from "../types/produto";
import { useCart } from "@/hooks/useCart";
import toast from "react-hot-toast";

interface ProdutoCardProps {
  produto: Produto;
}

export const ProdutoCard = ({ produto }: ProdutoCardProps) => {
  const { addItem } = useCart();
  const formatadorMoeda = new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  });

  const handleAddToCart = () => {
    addItem(produto);
    toast.success(`${produto.nome} adicionado ao carrinho!`);
  };

  return (
    <Card className="flex flex-col">
      <CardHeader>
        <CardTitle className="line-clamp-1">{produto.nome}</CardTitle>
        <CardDescription className="h-10 line-clamp-2">
          {produto.descricao}
        </CardDescription>
      </CardHeader>
      <CardContent className="flex-grow">
        <div className="text-3xl font-bold">
          {formatadorMoeda.format(produto.preco)}
        </div>
        <p className="text-xs text-muted-foreground">
          {produto.quantidadeEstoque > 0
            ? `${produto.quantidadeEstoque} em estoque`
            : "Sem estoque"}
        </p>
      </CardContent>
      <CardFooter>
        <Button
          className="w-full"
          disabled={produto.quantidadeEstoque === 0}
          onClick={handleAddToCart}
        >
          Adicionar ao carrinho
        </Button>
      </CardFooter>
    </Card>
  );
};
