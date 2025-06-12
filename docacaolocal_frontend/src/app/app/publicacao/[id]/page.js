"use client";
import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { fetchApi } from "@/service/api";
import DetalhesPublicacao from "../../../components/DetalhesPublicacao";
import UsuarioPublicacao from "../../../components/UsuarioPublicacao";
import MapaMostrarPublicacao from "../../../components/MapaMostrarPublicacao";
import BotaoMatch from "../../../components/BotaoMatch";

export default function PaginaPublicacao() {
  const { id } = useParams();
  const [publicacao, setPublicacao] = useState(null);
  const [erro, setErro] = useState("");

  useEffect(() => {
    fetchApi(`/api/publicacoes/${id}`)
      .then(setPublicacao)
      .catch(() => setErro("Erro ao carregar publicação."));
  }, [id]);

  if (erro) return <div>{erro}</div>;
  if (!publicacao) return <div>Carregando...</div>;

  return (
    <div className="flex flex-col gap-6 p-6">
      <UsuarioPublicacao usuario={publicacao.usuario} />
      <DetalhesPublicacao publicacao={publicacao} />
      <MapaMostrarPublicacao lat={publicacao.latitude} lng={publicacao.longitude} />
      <BotaoMatch publicacaoId={publicacao.id} quantidadeDisponivel={publicacao.quantidade} />
    </div>
  );
}