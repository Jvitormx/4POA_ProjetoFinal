export default function DetalhesPublicacao({ publicacao }) {
  return (
    <div className="flex gap-6">
      <img src={publicacao.imagemUrl} alt="Item" className="w-32 h-32 rounded-lg" />
      <div>
        <div>{publicacao.inicioColeta} - {publicacao.fimColeta} • {publicacao.distancia} km</div>
        <h3>{publicacao.titulo}</h3>
        <div>{publicacao.categoria}</div>
        <p>{publicacao.descricao}</p>
        <div>Quantidade: {publicacao.quantidade}</div>
        {publicacao.urgente && <span>Urgente</span>}
        {publicacao.permiteEntrega && <span>Permite entrega</span>}
      </div>
    </div>
  );
}