"use client";
import { useEffect, useState } from "react";
import { fetchApi } from "@/service/api";
import ItemConexao from "@/app/components/ItemConexao";
import PainelDetalheConexao from "@/app/components/PainelDetalheConexao";

export default function Conexoes() {
  const [matches, setMatches] = useState([]);
  const [usuario, setUsuario] = useState(null);
  const [matchSelecionado, setMatchSelecionado] = useState(null);

  useEffect(() => {
    const userStr = localStorage.getItem("usuario");
    if (!userStr) return;
    const user = JSON.parse(userStr);
    setUsuario(user);

    fetchApi(`/api/matches/usuario/${user.id}`)
      .then(setMatches)
      .catch(console.error);
  }, []);

  if (!usuario) return <div>Carregando...</div>;

  return (
    <div className="flex">
      {/* Lista de conexões */}
      <div className="w-1/3 p-6 border-r">
        <h2 className="text-xl font-bold mb-4">Lista de conexões</h2>
        <div className="flex flex-col gap-2">
          {matches.length === 0 && <div>Nenhuma conexão encontrada.</div>}
          {matches.map((match) => (
            <ItemConexao
              key={match.id}
              match={match}
              usuario={usuario}
              onClick={() => setMatchSelecionado(match)}
              selecionado={matchSelecionado?.id === match.id}
            />
          ))}
        </div>
      </div>
      {/* Painel de detalhes */}
      <div className="flex-1 p-6">
        {matchSelecionado ? (
          <PainelDetalheConexao
            match={matchSelecionado}
            usuario={usuario}
            onStatusChange={novoStatus => {
              // Atualize o status do match no backend e frontend
              fetchApi(`/api/matches/${matchSelecionado.id}/status?status=${novoStatus}`, { method: "POST" })
                .then(matchAtualizado => {
                  setMatches(ms => ms.map(m => m.id === matchAtualizado.id ? matchAtualizado : m));
                  setMatchSelecionado(matchAtualizado);
                });
            }}
          />
        ) : (
          <div className="text-gray-500">Selecione uma conexão para ver detalhes.</div>
        )}
      </div>
    </div>
  );
}