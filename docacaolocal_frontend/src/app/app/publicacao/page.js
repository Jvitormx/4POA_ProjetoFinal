"use client";
import { useState } from "react";
import { fetchApi } from "@/service/api";
import { useRouter } from "next/navigation";
import dynamic from "next/dynamic";
const MapaPublicacao = dynamic(() => import("../../components/MapaPublicacao"), { ssr: false });

export default function CadastroPublicacao() {
  const [tipo, setTipo] = useState("OFERTA");
  const [titulo, setTitulo] = useState("");
  const [descricao, setDescricao] = useState("");
  const [categoria, setCategoria] = useState("");
  const [quantidade, setQuantidade] = useState(1);
  const [inicioColeta, setInicioColeta] = useState("");
  const [fimColeta, setFimColeta] = useState("");
  const [endereco, setEndereco] = useState("");
  const [latLng, setLatLng] = useState(null); // Novo estado
  const [permiteEntrega, setPermiteEntrega] = useState(false);
  const [urgente, setUrgente] = useState(false);
  const [erro, setErro] = useState("");
  const router = useRouter();

  function toISOStringWithSeconds(dt) {
    if (!dt) return undefined;
    // Se vier só até minutos, adiciona ":00"
    if (dt.length === 16) dt += ":00";
    // Cria objeto Date e retorna em ISO (com fuso horário)
    const date = new Date(dt);
    return date.toISOString();
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setErro("");
    const usuario = JSON.parse(localStorage.getItem("usuario"));
    if (!usuario) {
      setErro("Usuário não autenticado.");
      return;
    }
    if (!latLng) {
      setErro("Selecione um local no mapa.");
      return;
    }
    const dto = {
      tipo: tipo.toUpperCase(),
      titulo,
      descricao,
      categoria,
      quantidade,
      endereco,
      permiteEntrega,
      urgente,
      latitude: latLng.lat,
      longitude: latLng.lng,
      inicioColeta: tipo === "OFERTA" ? toISOStringWithSeconds(inicioColeta) : undefined,
      fimColeta: tipo === "OFERTA" ? toISOStringWithSeconds(fimColeta) : undefined,
    };
    console.log("DTO enviado:", dto); // <-- Adicione esta linha
    try {
      await fetchApi(`/api/publicacoes?usuarioId=${usuario.id}`, {
        method: "POST",
        body: JSON.stringify(dto),
      });
      router.push("/app/feed");
    } catch (err) {
      setErro("Erro ao cadastrar publicação.");
    }
  }

  return (
    <div className="max-w-lg mx-auto mt-8 bg-white rounded shadow p-6">
      <h2 className="text-2xl font-bold mb-4 text-[#1b2845]">Nova Publicação</h2>
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <div>
          <label className="font-medium text-[#1b2845]">Tipo</label>
          <select value={tipo} onChange={e => setTipo(e.target.value)} className="w-full border rounded p-2">
            <option value="OFERTA">Oferta</option>
            <option value="PEDIDO">Pedido</option>
          </select>
        </div>
        <div>
          <label className="font-medium text-[#1b2845]">Título</label>
          <input value={titulo} onChange={e => setTitulo(e.target.value)} required className="w-full border rounded p-2" />
        </div>
        <div>
          <label className="font-medium text-[#1b2845]">Descrição</label>
          <textarea value={descricao} onChange={e => setDescricao(e.target.value)} className="w-full border rounded p-2" />
        </div>
        <div>
          <label className="font-medium text-[#1b2845]">Categoria</label>
          <input value={categoria} onChange={e => setCategoria(e.target.value)} required className="w-full border rounded p-2" />
        </div>
        <div>
          <label className="font-medium text-[#1b2845]">Quantidade</label>
          <input type="number" min={1} value={quantidade} onChange={e => setQuantidade(Number(e.target.value))} required className="w-full border rounded p-2" />
        </div>
        {tipo === "OFERTA" && (
          <div className="flex gap-2">
            <div className="flex-1">
              <label className="font-medium text-[#1b2845]">Início da Coleta</label>
              <input type="datetime-local" value={inicioColeta} onChange={e => setInicioColeta(e.target.value)} className="w-full border rounded p-2" />
            </div>
            <div className="flex-1">
              <label className="font-medium text-[#1b2845]">Fim da Coleta</label>
              <input type="datetime-local" value={fimColeta} onChange={e => setFimColeta(e.target.value)} className="w-full border rounded p-2" />
            </div>
          </div>
        )}
        <div>
          <label className="font-medium text-[#1b2845]">Selecione o endereço no mapa</label>
          <MapaPublicacao setEndereco={setEndereco} setLatLng={setLatLng} />
        </div>
        <div>
          <label className="font-medium text-[#1b2845]">Endereço</label>
          <input value={endereco} onChange={e => setEndereco(e.target.value)} required className="w-full border rounded p-2" />
        </div>
        <div className="flex gap-4">
          <label className="flex items-center gap-2 text-[#1b2845]">
            <input type="checkbox" checked={permiteEntrega} onChange={e => setPermiteEntrega(e.target.checked)} />
            Permite entrega
          </label>
          <label className="flex items-center gap-2 text-[#1b2845]">
            <input type="checkbox" checked={urgente} onChange={e => setUrgente(e.target.checked)} />
            Urgente
          </label>
        </div>
        {erro && <div className="text-red-600">{erro}</div>}
        <button type="submit" className="bg-[#5899e2] text-white px-4 py-2 rounded hover:bg-[#1b2845] transition">
          Cadastrar
        </button>
      </form>
    </div>
  );
}