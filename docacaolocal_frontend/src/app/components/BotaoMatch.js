import { useState } from "react";
import { fetchApi } from "@/service/api";
import ModalMatch from "./ModalMatch";

export default function BotaoMatch({ publicacaoId, quantidadeDisponivel }) {
  const [modalAberto, setModalAberto] = useState(false);
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState("");

  const handleConfirm = async ({ quantidade, mensagem }) => {
    setLoading(true);
    setMsg("");
    setModalAberto(false);
    const usuario = JSON.parse(localStorage.getItem("usuario"));
    try {
      // 1. Cria o match
      const match = await fetchApi("/api/matches/manual", {
        method: "POST",
        body: JSON.stringify({
          publicacaoId,
          solicitanteId: usuario.id,
          quantidade,
        }),
      });
      // 2. (Opcional) Envia mensagem vinculada ao match, se backend suportar
      if (mensagem && match.id) {
        await fetchApi("/api/mensagens", {
          method: "POST",
          body: JSON.stringify({
            matchId: match.id,
            remetenteId: usuario.id,
            conteudo: mensagem,
          }),
        });
      }
      setMsg("Match solicitado com sucesso!");
    } catch {
      setMsg("Erro ao solicitar match.");
    }
    setLoading(false);
  };

  return (
    <div>
      <button
        onClick={() => setModalAberto(true)}
        disabled={loading}
        className="bg-purple-600 text-white px-4 py-2 rounded-lg"
      >
        {loading ? "Enviando..." : "Match →"}
      </button>
      <ModalMatch
        aberto={modalAberto}
        onClose={() => setModalAberto(false)}
        onConfirm={handleConfirm}
        maxQuantidade={quantidadeDisponivel}
      />
      {msg && <div>{msg}</div>}
    </div>
  );
}