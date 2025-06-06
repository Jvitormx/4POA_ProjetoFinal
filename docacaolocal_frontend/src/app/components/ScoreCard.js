export default function ScoreCard({ usuario }) {
  return (
    <div className="rounded-xl bg-[#1b2845] p-6 flex flex-col gap-2 shadow text-[#65afff]">
      <span className="text-3xl font-bold">Score</span>
      <span className="text-lg">{usuario.nome}</span>
      <div className="flex gap-2 mt-2">
        <button className="bg-[#5899e2] px-4 py-2 rounded font-medium text-white shadow">
          Ofertas recebidas
        </button>
        <button className="bg-[#65afff] px-4 py-2 rounded font-medium text-[#1b2845] shadow border border-[#5899e2]">
          Solicitações atendidas
        </button>
      </div>
    </div>
  );
}