import { useEffect, useState } from "react";
import { fetchApi } from "@/service/api";

export default function PainelDetalheConexao({ match, usuario, onStatusChange }) {
  const souSolicitante = match.usuario.id === usuario.id;
  const outroUsuario = souSolicitante ? match.publicacao.usuario : match.usuario;
  const [mensagens, setMensagens] = useState([]);
  const [mensagemResposta, setMensagemResposta] = useState("");

  useEffect(() => {
    // Busca mensagens do match
    fetchApi(`/api/mensagens?matchId=${match.id}`).then(setMensagens);
  }, [match.id]);

  return (
    <div>
      <h2 className="text-2xl font-bold mb-2">
        {souSolicitante ? outroUsuario.nome : "Solicitação recebida de " + outroUsuario.nome}
      </h2>
      {/* Imagem da publicação */}
      <div className="mb-4">
        <img src={match.publicacao.imagemUrl} alt="Imagem da publicação" className="w-32 h-32 object-cover rounded" />
        <div className="font-semibold">{match.publicacao.titulo}</div>
        <div className="text-sm text-gray-600">
          Quantidade selecionada: {match.quantidade}
        </div>
      </div>
      {/* Mensagens */}
      <div className="mb-4">
        {mensagens.map(msg => (
          <div key={msg.id} className={`mb-2 ${msg.remetente.id === usuario.id ? "text-right" : "text-left"}`}>
            <span className="inline-block bg-gray-200 rounded px-2 py-1">{msg.conteudo}</span>
          </div>
        ))}
      </div>
      {/* Painel de ação */}
      {souSolicitante ? (
        <div className="text-yellow-700 bg-yellow-100 rounded p-2">
          Aguardando confirmação do usuário que fez a publicação.
        </div>
      ) : (
        <div>
          <textarea
            className="w-full border rounded p-2 mb-2"
            placeholder="Escreva sua mensagem para o solicitante"
            value={mensagemResposta}
            onChange={e => setMensagemResposta(e.target.value)}
          />
          <div className="flex gap-2">
            <button
              className="bg-green-500 text-white px-4 py-2 rounded"
              onClick={async () => {
                if (mensagemResposta) {
                  await fetchApi("/api/mensagens", {
                    method: "POST",
                    body: JSON.stringify({
                      matchId: match.id,
                      remetenteId: usuario.id,
                      conteudo: mensagemResposta,
                    }),
                  });
                  setMensagemResposta(""); // Limpa textarea após envio
                }
                onStatusChange("CONFIRMADO");
              }}
            >
              Confirmar
            </button>
            <button
              className="bg-red-500 text-white px-4 py-2 rounded"
              onClick={() => onStatusChange("RECUSADO")}
            >
              Recusar
            </button>
            <button
              className="bg-yellow-400 text-white px-4 py-2 rounded"
              onClick={() => onStatusChange("PENDENTE")}
            >
              Pendente
            </button>
          </div>
        </div>
      )}
    </div>
  );
}