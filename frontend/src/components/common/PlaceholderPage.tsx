interface PlaceholderPageProps {
  title: string;
  description?: string;
}

export const PlaceholderPage = ({ 
  title, 
  description = "Página em construção." 
}: PlaceholderPageProps) => (
  <div className="flex flex-col items-center justify-center min-h-[50vh] text-center">
    <h2 className="text-3xl font-bold tracking-tight mb-2">{title}</h2>
    <p className="text-muted-foreground">{description}</p>
  </div>
);
