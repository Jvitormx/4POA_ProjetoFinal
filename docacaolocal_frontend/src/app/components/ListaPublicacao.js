import ItemPublicacao from "./ItemPublicacao";

export default function ListaPublicacao({ publicacoes }) {
  if (!publicacoes.length) {
    return <div className="text-center text-[#1b2845]">Nenhuma publicação encontrada.</div>;
  }
  return (
    <div className="flex flex-col gap-4">
      {publicacoes.map(pub => (
        <ItemPublicacao key={pub.id} publicacao={pub} />
      ))}
    </div>
  );
}