"use client";
import { useEffect, useState } from "react";
import ScoreCard from "../../components/ScoreCard";
import FeedFiltro from "../../components/FeedFiltro";
import FeedHeader from "../../components/FeedHeader";
import ListaPublicacao from "../../components/ListaPublicacao";

export default function Feed() {
  const [usuario, setUsuario] = useState(null);
  const [tipo, setTipo] = useState("OFERTA"); // ou "SOLICITACAO"
  const [publicacoes, setPublicacoes] = useState([]);

  useEffect(() => {
    const userStr = localStorage.getItem("usuario");
    if (userStr) {
      const user = JSON.parse(userStr);
      setUsuario(user);

      // Exemplo de fetch do feed (ajuste endpoint conforme backend)
      fetch(`/api/publicacoes/feed?usuarioId=${user.id}&tipo=${tipo}`)
        .then((res) => res.json())
        .then(setPublicacoes);
    }
  }, [tipo]);

  if (!usuario) {
    return <div>Carregando...</div>;
  }

  return (
    <div className="flex flex-col gap-6 p-6">
      <ScoreCard usuario={usuario} />
      <FeedFiltro tipo={tipo} setTipo={setTipo} />
      <FeedHeader usuario={usuario} />
      <ListaPublicacao publicacoes={publicacoes} />
    </div>
  );
}
