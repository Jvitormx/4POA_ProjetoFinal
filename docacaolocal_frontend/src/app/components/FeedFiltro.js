export default function FeedFilters({ tipo, setTipo }) {
  return (
    <div className="flex gap-2 justify-center">
      <button
        className={`px-4 py-2 rounded-l-full ${
          tipo === "OFERTA"
            ? "bg-[#5899e2] text-white"
            : "bg-[#65afff] text-[#1b2845] border border-[#5899e2]"
        }`}
        onClick={() => setTipo("OFERTA")}
      >
        Ofertas
      </button>
      <button
        className={`px-4 py-2 rounded-r-full ${
          tipo === "PEDIDO"
            ? "bg-[#5899e2] text-white"
            : "bg-[#65afff] text-[#1b2845] border border-[#5899e2]"
        }`}
        onClick={() => setTipo("PEDIDO")}
      >
        Solicitações
      </button>
    </div>
  );
}