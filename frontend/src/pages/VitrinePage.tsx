import { useState, useEffect } from "react";
import toast from "react-hot-toast";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from "../components/ui/pagination";
import api from "../services/api";
import { type Produto } from "../types/produto";
import { ProdutoCard } from "@/components/ProdutoCard";

// --- Componente Principal da Página ---
const VitrinePage = () => {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalItems, setTotalItems] = useState(0);

  const itemsPerPage = 9;

  useEffect(() => {
    const fetchProdutos = async () => {
      setIsLoading(true);
      try {
        const response = await api.get(
          `/produtos?page=${currentPage}&size=${itemsPerPage}&sort=nome,asc`
        );
        setProdutos(response.data.content);
        setTotalPages(response.data.totalPages);
        setTotalItems(response.data.totalElements);
      } catch (error) {
        console.error("Falha ao buscar produtos", error);
        toast.error("Não foi possível carregar os produtos.");
      } finally {
        setIsLoading(false);
      }
    };
    fetchProdutos();
  }, [currentPage]);

  if (isLoading) {
    return <p>Carregando produtos...</p>;
  }

  return (
    <div className="flex flex-col gap-8">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Nossos Produtos</h2>
        <p className="text-muted-foreground">
          Encontramos {totalItems} produtos para você.
        </p>
      </div>

      {produtos.length > 0 ? (
        <div className="grid gap-4 md:gap-8 sm:grid-cols-2 lg:grid-cols-3">
          {produtos.map((produto) => (
            <ProdutoCard key={produto.id} produto={produto} />
          ))}
        </div>
      ) : (
        <div className="text-center py-16">
          <h3 className="text-xl font-semibold">Nenhum produto encontrado</h3>
          <p className="text-muted-foreground">
            Não há produtos cadastrados nesta página. Tente outra.
          </p>
        </div>
      )}

      {totalPages > 1 && (
        <Pagination>
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious
                href="#"
                onClick={(e) => {
                  e.preventDefault();
                  setCurrentPage((p) => Math.max(0, p - 1));
                }}
                className={
                  currentPage === 0 ? "pointer-events-none opacity-50" : ""
                }
              />
            </PaginationItem>
            <PaginationItem>
              <PaginationNext
                href="#"
                onClick={(e) => {
                  e.preventDefault();
                  setCurrentPage((p) => Math.min(totalPages - 1, p + 1));
                }}
                className={
                  currentPage === totalPages - 1
                    ? "pointer-events-none opacity-50"
                    : ""
                }
              />
            </PaginationItem>
          </PaginationContent>
        </Pagination>
      )}
    </div>
  );
};

export default VitrinePage;
