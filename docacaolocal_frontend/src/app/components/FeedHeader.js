export default function FeedHeader({ usuario }) {
  return (
    <div className="bg-[#5899e2] text-white text-center py-2 rounded font-semibold">
      Ofertas próximas de você
      <span className="mx-2 text-sm text-[#65afff]">
        Dentro de {usuario.raioBuscaKm || 5} km • Info
      </span>
    </div>
  );
}