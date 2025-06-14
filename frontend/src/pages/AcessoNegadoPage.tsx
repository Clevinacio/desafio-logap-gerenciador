import { Link } from "react-router-dom";
import { ShieldAlert } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

const AcessoNegadoPage = () => {
  return (
    <div className="flex items-center justify-center py-16">
      <Card className="w-full max-w-lg text-center">
        <CardHeader>
          <div className="mx-auto bg-destructive/10 rounded-full p-3 w-fit">
            <ShieldAlert className="h-10 w-10 text-destructive" />
          </div>
          <CardTitle className="mt-4 text-2xl">Acesso Negado</CardTitle>
          <CardDescription>
            Você não tem permissão para visualizar esta página. Se você acredita
            que isso é um erro, por favor, contate o administrador do sistema.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Button asChild>
            <Link to="/dashboard">Voltar para o Dashboard</Link>
          </Button>
        </CardContent>
      </Card>
    </div>
  );
};

export default AcessoNegadoPage;
