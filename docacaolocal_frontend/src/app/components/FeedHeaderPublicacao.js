export default function FeedHeaderPublicacao({ tipo, onBack }) {
  return (
    <div className="flex items-center justify-between">
      <button onClick={onBack}>&larr;</button>
      <span>{tipo === "OFERTA" ? "Oferta" : "Pedido"}</span>
      <div>
        <button>🔖</button>
        <button>⋮</button>
      </div>
    </div>
  );
}