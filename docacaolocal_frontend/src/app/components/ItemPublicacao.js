import Link from "next/link";

export default function ItemPublicacao({ publicacao }) {
  return (
    <div className="flex items-center gap-4 bg-[#65afff] rounded shadow p-4 border border-[#5899e2]">
      <div className="w-16 h-16 bg-[#5899e2] rounded flex items-center justify-center">
        {/* Imagem ou ícone */}
      </div>
      <div className="flex-1">
        <div className="font-semibold text-[#1b2845]">{publicacao.titulo}</div>
        <div className="text-sm text-[#5899e2]">
          {publicacao.categoria} • {publicacao.distancia?.toFixed(1) || "?"} km
        </div>
        <div className="text-xs text-[#1b2845] opacity-70">{publicacao.descricao}</div>
      </div>
      <Link href={`/app/publicacao/${publicacao.id}`}>
        <span className="text-[#1b2845] hover:underline">Ver</span>
      </Link>
    </div>
  );
}