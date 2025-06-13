import { useParams, Link } from "react-router-dom";
import { CheckCircle } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

const PedidoConfirmadoPage = () => {
  const { id } = useParams<{ id: string }>();

  return (
    <div className="flex items-center justify-center py-16">
      <Card className="w-full max-w-lg text-center">
        <CardHeader>
          <div className="mx-auto bg-green-100 dark:bg-green-900/50 rounded-full p-3 w-fit">
            <CheckCircle className="h-10 w-10 text-green-500" />
          </div>
          <CardTitle className="mt-4 text-2xl">
            Pedido Realizado com Sucesso!
          </CardTitle>
          <CardDescription>
            Seu pedido <strong>#{id}</strong> foi recebido e está pendente de
            aprovação. Você pode acompanhar o status na página "Meus Pedidos".
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex justify-center gap-4">
            <Button asChild>
              <Link to="/">Voltar para a Página Inicial</Link>
            </Button>
            <Button asChild variant="outline">
              <Link to="/meus-pedidos">Ver Meus Pedidos</Link>
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default PedidoConfirmadoPage;
