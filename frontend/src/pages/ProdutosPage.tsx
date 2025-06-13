import React, { useState, useEffect } from "react";
import { MoreHorizontal } from "lucide-react";
import toast from "react-hot-toast";
import { isAxiosError } from "axios";
import { NumericFormat } from "react-number-format";

import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import api from "@/services/api";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { type Produto } from "@/types/produto";
import { type PageableResponse } from "@/types/pageableResponse";

interface CurrencyInputProps {
  value: number | null;
  onValueChange: (value: number | null) => void;
  className?: string;
  placeholder?: string;
  disabled?: boolean;
}

const CurrencyInput = React.forwardRef<HTMLInputElement, CurrencyInputProps>(
  ({ value, onValueChange, ...props }, ref) => {
    return (
      <NumericFormat
        prefix="R$ "
        thousandSeparator="."
        decimalSeparator=","
        decimalScale={2}
        fixedDecimalScale={true}
        value={value}
        onValueChange={(values) => {
          onValueChange(
            values.floatValue === undefined ? null : values.floatValue
          );
        }}
        customInput={Input}
        getInputRef={ref}
        {...props}
      />
    );
  }
);

// --- Componente do Formulário/Dialog ---

interface ProdutoFormProps {
  produto?: Produto | null;
  onSave: (produtoData: Partial<Produto>) => Promise<void>;
  onClose: () => void;
}

const ProdutoFormDialog = ({ produto, onSave, onClose }: ProdutoFormProps) => {
  const [nome, setNome] = useState(produto?.nome || "");
  const [descricao, setDescricao] = useState(produto?.descricao || "");
  const [preco, setPreco] = useState(produto?.preco);
  const [quantidadeEstoque, setquantidadeEstoque] = useState(
    produto?.quantidadeEstoque
  );
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      const dataToSave = produto
        ? { id: produto.id, quantidadeEstoque }
        : { nome, descricao, preco, quantidadeEstoque };
      await onSave(dataToSave);
    } finally {
      setIsLoading(false);
    }
  };

  const isEditMode = !!produto;

  return (
    <DialogContent className="sm:max-w-[480px]">
      <DialogHeader>
        <DialogTitle>
          {isEditMode ? "Atualizar Estoque" : "Adicionar Novo Produto"}
        </DialogTitle>
        <DialogDescription>
          {isEditMode
            ? `Atualize a quantidade em estoque para ${produto.nome}.`
            : "Preencha os dados para cadastrar um novo produto."}
        </DialogDescription>
      </DialogHeader>
      <form onSubmit={handleSubmit}>
        <div className="grid gap-4 py-4">
          {!isEditMode && (
            <>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="nome" className="text-right">
                  Nome
                </Label>
                <Input
                  id="nome"
                  value={nome}
                  onChange={(e) => setNome(e.target.value)}
                  className="col-span-3"
                  required
                />
              </div>
              <div className="grid grid-cols-4 items-start gap-4">
                <Label htmlFor="descricao" className="text-right pt-2">
                  Descrição
                </Label>
                <Textarea
                  id="descricao"
                  value={descricao}
                  onChange={(e) => setDescricao(e.target.value)}
                  className="col-span-3"
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="preco" className="text-right">
                  Preço (R$)
                </Label>
                <CurrencyInput
                  value={preco ?? null}
                  onValueChange={(value) =>
                    setPreco(value === null ? undefined : value)
                  }
                  className="col-span-3"
                  placeholder="R$ 0,00"
                />
              </div>
            </>
          )}
          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="quantidadeEstoque" className="text-right">
              Estoque
            </Label>
            <Input
              id="quantidadeEstoque"
              type="number"
              value={quantidadeEstoque || ""}
              onChange={(e) =>
                setquantidadeEstoque(parseInt(e.target.value, 10))
              }
              placeholder="0"
              className="col-span-3 [appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none"
              required
            />
          </div>
        </div>
        <DialogFooter>
          <Button
            type="button"
            variant="ghost"
            onClick={onClose}
            disabled={isLoading}
          >
            Cancelar
          </Button>
          <Button type="submit" disabled={isLoading}>
            {isLoading ? "Salvando..." : "Salvar"}
          </Button>
        </DialogFooter>
      </form>
    </DialogContent>
  );
};

// --- Componente Principal da Página ---
const ProdutosPage = () => {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [pageInfo, setPageInfo] = useState<PageableResponse<Produto> | null>(
    null
  );
  const [isLoading, setIsLoading] = useState(true);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [isAlertOpen, setIsAlertOpen] = useState(false);
  const [editingProduto, setEditingProduto] = useState<Produto | null>(null);
  const [produtoParaExcluir, setProdutoParaExcluir] = useState<Produto | null>(
    null
  );
  const formatadorMoeda = new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  });

  const fetchProdutos = async () => {
    setIsLoading(true);
    try {
      const response = await api.get("/produtos");
      const pageableData: PageableResponse<Produto> = response.data;
      setProdutos(pageableData.content);
      setPageInfo(pageableData);
    } catch (error) {
      console.error("Falha ao buscar produtos", error);
      toast.error("Não foi possível carregar os produtos.");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchProdutos();
  }, []);

  const handleOpenCreate = () => {
    setEditingProduto(null);
    setIsDialogOpen(true);
  };

  const handleOpenEdit = (produto: Produto) => {
    setEditingProduto(produto);
    setIsDialogOpen(true);
  };

  const handleOpenDeleteAlert = (produto: Produto) => {
    setProdutoParaExcluir(produto);
    setIsAlertOpen(true);
  };

  const handleCloseDialog = () => {
    setIsDialogOpen(false);
    setEditingProduto(null);
  };

  const handleSaveProduto = async (produtoData: Partial<Produto>) => {
    try {
      if (produtoData.id) {
        await api.patch(`/produtos/${produtoData.id}/estoque`, {
          novaQuantidade: produtoData.quantidadeEstoque,
        });
        toast.success("Estoque atualizado com sucesso!");
      } else {
        await api.post("/produtos", produtoData);
        toast.success("Produto criado com sucesso!");
      }
      handleCloseDialog();
      fetchProdutos();
    } catch (err) {
      if (isAxiosError(err) && err.response?.data) {
        const responseData = err.response.data;
        if (
          typeof responseData === "object" &&
          !Array.isArray(responseData) &&
          responseData !== null
        ) {
          const firstError = Object.values(responseData)[0];
          toast.error(String(firstError));
        } else {
          toast.error(responseData.message || "Falha ao salvar produto.");
        }
      } else {
        toast.error("Ocorreu um erro inesperado.");
      }
      throw err;
    }
  };

  const handleConfirmDelete = async () => {
    if (!produtoParaExcluir) return;
    try {
      await api.delete(`/produtos/${produtoParaExcluir.id}`);
      toast.success(
        `Produto "${produtoParaExcluir.nome}" excluído com sucesso.`
      );
      setIsAlertOpen(false);
      setProdutoParaExcluir(null);
      fetchProdutos();
    } catch (err) {
      if (isAxiosError(err) && err.response?.data) {
        toast.error(
          err.response.data.message || "Não foi possível excluir o produto."
        );
      } else {
        toast.error("Ocorreu um erro inesperado.");
      }
      setIsAlertOpen(false);
    }
  };

  if (isLoading) {
    return <p>Carregando produtos...</p>;
  }

  return (
    <div className="flex flex-col gap-4">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Produtos</h2>
          <p className="text-muted-foreground">
            Cadastre e gerencie os produtos da sua loja.
          </p>
        </div>
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger asChild>
            <Button onClick={handleOpenCreate}>Adicionar Produto</Button>
          </DialogTrigger>
          {isDialogOpen && (
            <ProdutoFormDialog
              produto={editingProduto}
              onSave={handleSaveProduto}
              onClose={handleCloseDialog}
            />
          )}
        </Dialog>
      </div>

      <Card>  
        <CardHeader>
          <CardTitle>Lista de Produtos</CardTitle>
          <CardDescription>
            Um total de {pageInfo?.totalElements || produtos.length} produtos
            cadastrados.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Nome</TableHead>
                <TableHead>Estoque</TableHead>
                <TableHead>Preço</TableHead>
                <TableHead>
                  <span className="sr-only">Ações</span>
                </TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {produtos.map((produto: Produto) => (
                <TableRow key={produto.id}>
                  <TableCell className="font-medium">{produto.nome}</TableCell>
                  <TableCell>
                    <Badge
                      variant={
                        produto.quantidadeEstoque > 0
                          ? "outline"
                          : "destructive"
                      }
                    >
                      {produto.quantidadeEstoque > 0
                        ? `${produto.quantidadeEstoque} em estoque`
                        : "Sem estoque"}
                    </Badge>
                  </TableCell>
                  <TableCell>{formatadorMoeda.format(produto.preco)}</TableCell>
                  <TableCell>
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button
                          aria-haspopup="true"
                          size="icon"
                          variant="ghost"
                        >
                          <MoreHorizontal className="h-4 w-4" />
                          <span className="sr-only">Toggle menu</span>
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end">
                        <DropdownMenuLabel>Ações</DropdownMenuLabel>
                        <DropdownMenuItem
                          onSelect={() => handleOpenEdit(produto)}
                        >
                          Atualizar Estoque
                        </DropdownMenuItem>
                        <DropdownMenuItem
                          className="text-destructive"
                          onSelect={() => handleOpenDeleteAlert(produto)}
                        >
                          Excluir
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <AlertDialog open={isAlertOpen} onOpenChange={setIsAlertOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Você tem certeza absoluta?</AlertDialogTitle>
            <AlertDialogDescription>
              Esta ação não pode ser desfeita. Isso irá excluir permanentemente
              o produto
              <span className="font-bold"> "{produtoParaExcluir?.nome}"</span>.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={() => setProdutoParaExcluir(null)}>
              Cancelar
            </AlertDialogCancel>
            <AlertDialogAction onClick={handleConfirmDelete}>
              Confirmar Exclusão
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
};

export default ProdutosPage;
